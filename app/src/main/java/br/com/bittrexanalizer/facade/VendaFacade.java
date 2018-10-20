package br.com.bittrexanalizer.facade;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.com.bittrexanalizer.database.dao.ConfiguracaoDAO;
import br.com.bittrexanalizer.database.dao.TickerDAO;
import br.com.bittrexanalizer.domain.Balance;
import br.com.bittrexanalizer.domain.Configuracao;
import br.com.bittrexanalizer.domain.Order;
import br.com.bittrexanalizer.domain.Ticker;
import br.com.bittrexanalizer.strategy.BalanceStrategy;
import br.com.bittrexanalizer.strategy.SellOrderStrategy;
import br.com.bittrexanalizer.utils.ConstantesUtil;
import br.com.bittrexanalizer.utils.EmailUtil;
import br.com.bittrexanalizer.utils.SessionUtil;

/**
 * Created by PauLinHo on 16/09/2017.
 */

public class VendaFacade {

    private TickerDAO tickerDAO;
    private boolean robotLigado = false;
    private EmailUtil emailUtil;
    private Context context;

    private final String ROBOT_VENDA = "VENDA ROBOT ";
    private final String ROBOT_ERROS = "ERROS ROBOT ";

    private StringBuilder moedasErros = new StringBuilder();

    private volatile LinkedList<Ticker> moedasHabilitadas;

    public void executar(Context context) {

        this.context = context;
        tickerDAO = new TickerDAO(context);

        Log.i("MENU", "VENDA FACADE");

        getConfiguracoes();

        moedasHabilitadas = new LinkedList<>();
        moedasHabilitadas = tickerDAO.findAllIsBought(true);

        robotLigado = Boolean.valueOf(SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.ROBOT_LIGADO));

        moedasErros.append("\r\r\r"+ROBOT_ERROS);

        emailUtil = new EmailUtil();

        if (moedasHabilitadas.size() == 0) {
            return;
        } else {

            //Verifica se o robot esta ligado
            if (robotLigado) {
                executarRobot();
                SessionUtil.getInstance().getMsgErros().append(moedasErros.toString());
            }else{
                return;
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


    private void executarRobot() {


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    getDados();

                    boolean vendido = false;

                    for(Ticker t : moedasHabilitadas) {

                        if (t.getAvisoStopGain().compareTo(t.getBid()) == -1) {
                            vendido = venderLimit(t);
                        }

                        if (t.getAvisoStopLoss().compareTo(t.getAsk()) == 1) {
                            vendido = venderLoss(t);
                        }

                        if (vendido) {
                            t.setBought(false);
                            tickerDAO.update(t);
                        }
                    }

                } catch (Exception e) {
                    moedasErros.append("ERRO: " + e.getMessage());
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


    private synchronized boolean venderLimit(Ticker ticker) {

        Order order = new Order();
        //verifica se tem uma order aberta para essa moeda

        //Atualiza o map de Balances
        BalanceStrategy.execute();

        //Se não existir a ordem pega a quantidade total dessa moeda
        Balance b = SessionUtil.getInstance().getMapBalances().get(ticker.getSigla());
        order.setQuantity(b.getBalance());
        order.setSigla(ticker.getSigla());

        //Pega o valor atual de venda
        order.setRate(ticker.getBid());

        //Realiza a venda
        boolean vendida = SellOrderStrategy.execute(order);

        if (vendida) {
            String mensagem = ROBOT_VENDA + "Moeda: " + ticker.getSigla();
            mensagem += "\n - Valor: " + ticker.getAsk();
            mensagem += "\n - Valor Limit: " + ticker.getAvisoStopGain();
            mensagem += "\n - Valor Lost: " + ticker.getAvisoStopLoss();
            enviarEmail(mensagem, ROBOT_VENDA);
        }else{
            moedasErros.append("Erro ao vender a moeda: "+ticker.getSigla());
        }

        return vendida;

    }

    private synchronized boolean venderLoss(Ticker ticker) {

        Order order = new Order();
        //verifica se tem uma order aberta para essa moeda

        //Atualiza o map de Balances
        BalanceStrategy.execute();

        //Se não existir a ordem pega a quantidade total dessa moeda
        Balance b = SessionUtil.getInstance().getMapBalances().get(ticker.getSigla());
        order.setQuantity(b.getBalance());
        order.setSigla(ticker.getSigla());

        //Pega o valor atual de venda
        order.setRate(ticker.getBid());

        //Realiza a venda
        boolean vendeu = false;
        vendeu = SellOrderStrategy.execute(order);

        if (!vendeu) {
            moedasErros.append("Não foi possível realizar a venda");
        }

        return vendeu;

    }

    private void enviarEmail(final String mensagem, final String operacao) {
        emailUtil.enviarEmail(context, ROBOT_VENDA + mensagem, operacao);

    }

    /**
     * Pega os dados do bittrex
     */
    public synchronized void getDados() {

        try {

            ExecutorService executorService = Executors.newCachedThreadPool();

            for (Ticker ticker : moedasHabilitadas) {
                executorService.execute(ticker);
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
            return;

        }
    }


}
