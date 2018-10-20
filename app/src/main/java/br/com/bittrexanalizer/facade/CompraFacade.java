package br.com.bittrexanalizer.facade;

import android.content.Context;
import android.util.Log;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.com.bittrexanalizer.analises.AnaliseRobot;
import br.com.bittrexanalizer.analises.IAnaliser;
import br.com.bittrexanalizer.database.dao.ConfiguracaoDAO;
import br.com.bittrexanalizer.database.dao.TickerDAO;
import br.com.bittrexanalizer.domain.Balance;
import br.com.bittrexanalizer.domain.Candle;
import br.com.bittrexanalizer.domain.Configuracao;
import br.com.bittrexanalizer.domain.Order;
import br.com.bittrexanalizer.domain.Ticker;
import br.com.bittrexanalizer.strategy.BalanceStrategy;
import br.com.bittrexanalizer.strategy.BuyOrderStrategy;
import br.com.bittrexanalizer.utils.CalculoUtil;
import br.com.bittrexanalizer.utils.ConstantesUtil;
import br.com.bittrexanalizer.utils.EmailUtil;
import br.com.bittrexanalizer.utils.SessionUtil;
import br.com.bittrexanalizer.utils.WebServiceUtil;

/**
 * Created by PauLinHo on 16/09/2017.
 */

public class CompraFacade {


    private TickerDAO tickerDAO;
    private Context context;
    public static Map<String, LinkedList<Candle>> mapCandles;
    private boolean robotLigado = false;
    private boolean devoParar;
    private EmailUtil emailUtil;
    private AnaliseRobot analiseRobot;
    private BigDecimal valorParaCompraRobot;
    private final String BTC = "BTC";

    private Balance balance;
    private final String ROBOT_COMPRA = "COMPRA ROBOT ";
    private final String ROBOT_ERROS = "ERROS ROBOT ";


    private StringBuilder moedasErros = new StringBuilder();
    private LinkedList<Ticker> moedasHabilitadas;
    private volatile LinkedList<Ticker> tickers;
    private LinkedList<Ticker> tickersDoBD;

    private int qtdeTickersPesquisar;
    private int tempoEsperoThread;

    public void executar(Context context) {

        this.context = context;
        tickerDAO = new TickerDAO(context);

        getConfiguracoes();

        Log.i("Bittrex", "Entrei");

        //pegando as variaveis do Sistema
        robotLigado = Boolean.valueOf(SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.ROBOT_LIGADO));
        qtdeTickersPesquisar = Integer.valueOf(SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.QTDE_TICKERS_PESQUISA));
        tempoEsperoThread = Integer.valueOf(SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.TEMPO_ESPERA_THREAD));
        valorParaCompraRobot = new BigDecimal(SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.VALOR_COMPRA_ROBOT)).setScale(8);

        SessionUtil.getInstance().setMaxCandleParaPesquisar(0);

        //iniciando objetos
        analiseRobot = new AnaliseRobot();
        moedasHabilitadas = new LinkedList<>();
        emailUtil = new EmailUtil();

        moedasErros.append("\r\r"+ROBOT_ERROS);

        //Verifica se o robot esta ligado
        if (robotLigado) {
            try {
                executarRobot();
            } catch (Exception e) {
                moedasErros.append(e.getMessage() + e.getStackTrace());
            } finally {
                SessionUtil.getInstance().getMsgErros().append(moedasErros.toString());
            }
        }

    }

    private void getConfiguracoes() {

        LinkedList<Configuracao> configuracoes = new LinkedList<>();
        configuracoes = new ConfiguracaoDAO(context).all();

        Map<String, String> mapConfiguracao = new HashMap<String, String>();

        if (configuracoes == null) {
            SessionUtil.getInstance().setMapConfiguracao(null);
            return;
        }

        for (Configuracao c : configuracoes) {
            mapConfiguracao.put(c.getPropriedade(), c.getValor());
        }

        SessionUtil.getInstance().setMapConfiguracao(mapConfiguracao);


    }


    /**
     * Realiza o processamento para analisar os valores de todas as moedas
     */
    private void executarRobot() {

        //atualiza os valores de BTC
        BalanceStrategy.execute();

        //pega o valor da moeda BTC, utilizada para realizar a compra
        balance = SessionUtil.getInstance().getMapBalances().get(BTC);

        //valor disponivel em BTC é maior ou igual o valor minimo para compra?
        if (!temSaldoBTC()) {
            devoParar = true;
            moedasErros.append("SEM SALDO");
            return;
        }

        //pegando todos os tickers do Banco de Dados
        tickersDoBD = tickerDAO.findAllTickers();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                do {

                    mapCandles = new ConcurrentHashMap<>(new HashMap<String, LinkedList<Candle>>());

                    getDados();

                    Set<String> keys = null;
                    if (mapCandles != null) {
                        keys = mapCandles.keySet();
                    }


                    for (String k : keys) {

                        LinkedList<Candle> lista = mapCandles.get(k);
                        if (lista.size() > 0) {
                            realizarAnalises(k, lista);

                        }
                    }

                    try {
                        if (!devoParar) {
                            Thread.sleep(tempoEsperoThread);
                        }
                    } catch (InterruptedException e) {
                        moedasErros.append("- " + e.getMessage() + " - " + e.getStackTrace());
                        e.printStackTrace();
                    }

                } while (!devoParar);

                if (!moedasHabilitadas.isEmpty()) {
                    return;
                }


            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public void getDados() {
        try {

            Boolean isAllTickers = false;
            /**
             * Faz a verificação se foi selecionados todas as moedas
             * ou se será calculado apenas nas moedas que o usuario esta analizando
             */
            if (SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.ALL_TICKERS)) {

                isAllTickers = new Boolean(SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.ALL_TICKERS));

                if (isAllTickers) {
                    tickers = new LinkedList<>();
                    Set<String> keys = SessionUtil.getInstance().getNomeExchanges().keySet();

                    for (String k : keys) {
                        Ticker t = new Ticker();

                        t.setSigla(k);

                        tickers.add(t);
                    }
                } else {
                    tickers = SessionUtil.getInstance().getTickers();
                }

            } else {
                tickers = SessionUtil.getInstance().getTickers();
            }

            ExecutorService executorService = Executors.newCachedThreadPool();
            int flagParar = 0;
            int i = 0;


            if ((SessionUtil.getInstance().getMaxCandleParaPesquisar() + qtdeTickersPesquisar) > tickers.size()) {
                i = SessionUtil.getInstance().getMaxCandleParaPesquisar();
                flagParar = tickers.size();
                SessionUtil.getInstance().setMaxCandleParaPesquisar(Integer.MIN_VALUE);
                devoParar = true;
            } else {
                flagParar = SessionUtil.getInstance().getMaxCandleParaPesquisar() + qtdeTickersPesquisar;
                i = SessionUtil.getInstance().getMaxCandleParaPesquisar();
            }

            for (; i < tickers.size(); i++) {

                if (i == flagParar) {
                    SessionUtil.getInstance().setMaxCandleParaPesquisar(SessionUtil.getInstance().getMaxCandleParaPesquisar() + qtdeTickersPesquisar);
                    break;
                }

                Candle candle = new Candle();
                candle.setSigla(tickers.get(i).getSigla());


                executorService.execute(candle);
            }

            executorService.shutdown();

            while (!executorService.isTerminated()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            moedasErros.append("Erro: " + e.getMessage());
            return;

        }
    }


    public void realizarAnalises(String sigla, LinkedList<Candle> candles) {

        boolean devoComprar = false;

        try {

            int valorOBV = analiseRobot.analizer(candles);

            if (valorOBV != IAnaliser.IDEAL_PARA_COMPRA) {
                devoComprar = false;
            } else {
                devoComprar = true;
            }


            if (devoComprar) {

                BalanceStrategy.execute();

                //atualiza o Balance
                balance = SessionUtil.getInstance().getMapBalances().get(BTC);

                //tem saldo?
                if (!temSaldoBTC()) {
                    devoParar = true;
                    return;
                }

                Ticker t = new Ticker();
                t.setSigla(sigla);
                t.setUrlApi(WebServiceUtil.getUrl() + t.getSigla().toLowerCase());

                t = localizarValorDoTicker(t);

                //realiza a compra
                boolean comprado = comprar(t);

                if (comprado) {

                    //calcula porcentagem
                    t.setAvisoStopLoss(CalculoUtil.getPorcentagemLoss(t.getBid()));
                    t.setAvisoStopGain(CalculoUtil.getPorcentagemLimit(t.getBid()));
                    t.setBought(true);

                    boolean jaExistia = false;

                    for (Ticker temp : tickersDoBD) {
                        //já existe?
                        if (temp.getSigla().toLowerCase().equals(t.getSigla().toLowerCase())) {
                            t.setId(temp.getId());
                            t.setNomeExchange(SessionUtil.getInstance().getNomeExchanges().get(t.getSigla()));
                            //atualizado
                            tickerDAO.update(t);
                            jaExistia = true;

                            continue;
                        }
                    }

                    //não existia
                    if (!jaExistia) {
                        t.setNomeExchange(SessionUtil.getInstance().getNomeExchanges().get(t.getSigla()));

                        tickerDAO.create(t);
                    }
                    String mensagem = ROBOT_COMPRA + "Moeda: " + t.getSigla();
                    mensagem += "\n - Valor: " + t.getAsk();
                    mensagem += "\n - Valor Limit: " + t.getAvisoStopGain();
                    mensagem += "\n - Valor Lost: " + t.getAvisoStopLoss();
                    enviarEmail(mensagem, ROBOT_COMPRA);

                    moedasHabilitadas.add(t);
                }

            }

        } catch (Exception e) {
            moedasErros.append("\t\r");
            moedasErros.append(e.getMessage());
        }

    }

    private Ticker localizarValorDoTicker(Ticker tic) {

        LinkedList<Ticker> tickersTemp = new LinkedList<>();
        tickersTemp.add(tic);

        ExecutorService executorService = Executors.newCachedThreadPool();
        for (Ticker t : tickersTemp) {
            executorService.execute(t);
        }

        executorService.shutdown();

        while (!executorService.isTerminated()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return tickersTemp.getFirst();

    }

    private boolean comprar(Ticker ticker) {
        Order order = new Order();
        order.setSigla(ticker.getSigla());

        //Calcula a quantidade da moeda que será comprada
        order.setQuantity(CalculoUtil.getQuantidadeASerComprada(valorParaCompraRobot, ticker.getAsk()));

        //Pega o valor atual de compra da moeda
        order.setRate(ticker.getAsk());

        boolean retorno = false;

        Log.i("Bittrex", "Comprar: " + ticker.getSigla() + " - Valor Venda: " + ticker.getAvisoStopGain() + " - Lost: " + ticker.getAvisoStopLoss());

        //executa a compra
        retorno = BuyOrderStrategy.execute(order);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return retorno;

    }

    private boolean temSaldoBTC() {

        if (balance.getBalance().compareTo(valorParaCompraRobot) == -1) {
            moedasErros.append("SEM SALDO");
            return false;
        }

        return true;

    }

    private void enviarEmail(final String mensagem, final String operacao) {

        emailUtil.enviarEmail(context, "COMPRA ROBOT: ", mensagem, operacao);

    }

}
