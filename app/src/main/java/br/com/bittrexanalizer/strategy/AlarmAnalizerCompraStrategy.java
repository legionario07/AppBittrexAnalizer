package br.com.bittrexanalizer.strategy;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.com.bittrexanalizer.R;
import br.com.bittrexanalizer.analises.IAnaliser;
import br.com.bittrexanalizer.analises.IFRAnaliser;
import br.com.bittrexanalizer.analises.MACDAnaliser;
import br.com.bittrexanalizer.analises.OBVAnaliser;
import br.com.bittrexanalizer.analises.OsciladorEstocasticoAnaliser;
import br.com.bittrexanalizer.database.dao.ConfiguracaoDAO;
import br.com.bittrexanalizer.domain.Candle;
import br.com.bittrexanalizer.domain.Configuracao;
import br.com.bittrexanalizer.domain.Ticker;
import br.com.bittrexanalizer.telas.MainActivityDrawer;
import br.com.bittrexanalizer.utils.ConstantesUtil;
import br.com.bittrexanalizer.utils.EmailUtil;
import br.com.bittrexanalizer.utils.SessionUtil;

/**
 * Created by PauLinHo on 16/09/2017.
 */

public class AlarmAnalizerCompraStrategy {

    private Context context;
    private boolean devoParar = false;
    public static Map<String, LinkedList<Candle>> mapCandles;
    private StringBuilder textoNotificacao;

    private StringBuilder moedasPositivasParaCompra = new StringBuilder();
    private StringBuilder moedasNegativasParaCompra = new StringBuilder();
    private StringBuilder moedasErros = new StringBuilder();

    private MACDAnaliser macdAnaliser;
    private IFRAnaliser ifrAnaliser;
    private OsciladorEstocasticoAnaliser osciladorEstocasticoAnaliser;
    private OBVAnaliser obvAnaliser;
    private EmailUtil emailUtil;

    private boolean verificarMACD = true;
    private boolean verificarIFR = true;
    private boolean verificarOE = true;
    private boolean verificarOBV = true;

    private int contador = 0;
    private int qtdeTickersPesquisar;
    private int tempoEsperoThread;

    private LinkedList<Ticker> tickers;

    public void executar(Context context) {

        this.context = context;

        moedasPositivasParaCompra.append("\n\rMOEDAS POSITIVAS PARA COMPRAR SEGUNDO AS ANALISES SOLICITADAS: \r\n");
        moedasNegativasParaCompra.append("\n\r\rMOEDAS NEGATIVAS PARA COMPRAR SEGUNDO AS ANALISES SOLICITADAS: \r\n");
        moedasErros.append("\r\r\rEXISTEM ERROS: \r\n");
        textoNotificacao = new StringBuilder();
        textoNotificacao.append("Moedas positivadas para Compra: \n");

        //Atualiza as configurações
        getConfiguracoes();

        qtdeTickersPesquisar = Integer.valueOf(SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.QTDE_TICKERS_PESQUISA));
        tempoEsperoThread = Integer.valueOf(SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.TEMPO_ESPERA_THREAD));

        SessionUtil.getInstance().setMaxCandleParaPesquisar(0);

        macdAnaliser = new MACDAnaliser();
        ifrAnaliser = new IFRAnaliser();
        osciladorEstocasticoAnaliser = new OsciladorEstocasticoAnaliser();
        obvAnaliser = new OBVAnaliser();
        emailUtil = new EmailUtil();

        verificarMACD = Boolean.valueOf(SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.MACD));
        verificarIFR = Boolean.valueOf(SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.IFR));
        verificarOE = Boolean.valueOf(SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.OSCILADOR_ESTOCASTICO));
        verificarOBV = Boolean.valueOf(SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.OBV));

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
                        Log.i("BIITREX", k);
                        contador++;

                        LinkedList<Candle> lista = mapCandles.get(k);
                        if (lista.size() > 0) {
                            realizarAnalises(k, lista);

                        }
                    }

                    try {
                        if(!devoParar) {
                            Thread.sleep(tempoEsperoThread);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } while (!devoParar);


                moedasPositivasParaCompra.append("\r\nFORAM ANALISADAS " + contador + " MOEDAS\n");
                moedasPositivasParaCompra.append("\r\n\nMACD: " + String.valueOf(verificarMACD).toUpperCase() + "\n");
                moedasPositivasParaCompra.append("\r\n\nIFR: " + String.valueOf(verificarIFR).toUpperCase() + " - VALOR: " +
                        SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.IFR_MIN));
                moedasPositivasParaCompra.append("\r\n\nOSCILADOR ESTOCASTICO: " + String.valueOf(verificarOE).toUpperCase() + " - VALOR: " +
                        SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.OE_TAXA_MIN));
                moedasPositivasParaCompra.append("\r\n\nOBV: " + String.valueOf(verificarOBV).toUpperCase() + " - QTDE_FECHAMENTOS: " +
                        SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.OBV_QTDE_FECHAMENTOS));


                if (SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.ENVIAR_EMAIL)) {
                    if (Boolean.valueOf(SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.ENVIAR_EMAIL))) {

                        StringBuilder textoEmail = new StringBuilder();
                        textoEmail.append(moedasPositivasParaCompra.toString());
                        textoEmail.append("\r\n");
                        textoEmail.append(moedasNegativasParaCompra.toString());
                        textoEmail.append("\r\n");
                        textoEmail.append(moedasErros.toString());

                        enviarEmail(textoEmail.toString(), "INFORMAÇÃO");
                    }
                }


                contador = 0;
                moedasPositivasParaCompra = new StringBuilder();
                moedasNegativasParaCompra = new StringBuilder();

            }
        });
        t.start();


    }

    public void realizarAnalises(String sigla, LinkedList<Candle> candles) {

        boolean devoComprar = false;

        try {
            Log.i("Sigla", sigla);
            if (verificarMACD) {
                int valorMACD = macdAnaliser.analizer(candles);

                if (valorMACD != IAnaliser.IDEAL_PARA_COMPRA) {
                    devoComprar = false;
                } else {
                    devoComprar = true;
                }

            }

            if (verificarIFR) {
                int valorIFR = ifrAnaliser.analizer(candles);

                if (valorIFR != IAnaliser.IDEAL_PARA_COMPRA) {
                    devoComprar = false;
                } else {
                    devoComprar = true;
                }

            }

            if (verificarOE) {
                int valorOE = osciladorEstocasticoAnaliser.analizer(candles);

                if (valorOE != IAnaliser.IDEAL_PARA_COMPRA) {
                    devoComprar = false;
                } else {
                    devoComprar = true;
                }

            }

            if (verificarOBV) {
                int valorOBV = obvAnaliser.analizer(candles);

                if (valorOBV != IAnaliser.IDEAL_PARA_COMPRA) {
                    devoComprar = false;
                } else {
                    devoComprar = true;
                }

            }


            if (devoComprar) {

                //String texto = TEXTO_EMAIL_COMPRA + sigla;

                textoNotificacao.append("\t"+sigla);

                moedasPositivasParaCompra.append("\r\t");
                moedasPositivasParaCompra.append(sigla);
                moedasPositivasParaCompra.append("\t");

            } else {

                moedasPositivasParaCompra.append("\r\t");
                moedasNegativasParaCompra.append(sigla);
                moedasNegativasParaCompra.append("\t");
            }

        } catch (Exception e) {
            moedasErros.append("\t\r");
            moedasErros.append(e.getMessage());

        }

    }

    private void enviarEmail(final String mensagem, final String operacao) {

        if (SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.ENVIAR_NOTIFICACAO)) {

            if (Boolean.valueOf(SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.ENVIAR_NOTIFICACAO))) {
                criarNotificacao(context, textoNotificacao.toString(), operacao);
            }
        }
        emailUtil.enviarEmail(context, mensagem, operacao);
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
            moedasErros.append("\r\n");
            moedasErros.append(e.getMessage());
            return;

        }
    }

    /**
     * Atualiza as configurações, pois se foi alterado algum parametro
     * será atualizado
     */
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
     * Criar uma notificaçao para exibir
     *
     * @param context
     */

    private void criarNotificacao(Context context, String texto, String operacao) {

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivityDrawer.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.bittrexanalizer)
                .setTicker("Aviso BITTREXANALIZER")
                .setContentTitle("Aviso BITTREXANALIZER")
                .setContentText(texto + operacao)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        Notification not = builder.build();
        not.vibrate = new long[]{150, 100, 6000, 100};
        not.flags = Notification.FLAG_AUTO_CANCEL;


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, not);

    }


}
