package br.com.bittrexanalizer.facade;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.com.bittrexanalizer.R;
import br.com.bittrexanalizer.database.dao.TickerDAO;
import br.com.bittrexanalizer.domain.Ticker;
import br.com.bittrexanalizer.telas.MainActivityDrawer;
import br.com.bittrexanalizer.utils.EmailUtil;
import br.com.bittrexanalizer.utils.WebServiceUtil;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Created by PauLinHo on 16/09/2017.
 */

public class AlarmTaxaFacade implements Runnable {

    private Ticker ticker;

    private TickerDAO tickerDAO;
    private Context context;
    private StringBuilder descricao;

    private EmailUtil emailUtil;

    private final String LOG = "BITTREX";
    private volatile LinkedList<Ticker> tickers;


    public synchronized void execute(Context context) {

        this.context = context;
        tickerDAO = new TickerDAO(context);
        emailUtil = new EmailUtil();

        Thread t = new Thread(this);
        t.start();

    }


    /**
     *
     * @param context
     * @param valor
     * @param aviso
     */
    private void criarNotificacao(Context context, BigDecimal valor, String aviso) {

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivityDrawer.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.bittrexanalizer)
                .setTicker("Aviso BITTREXANALIZER")
                .setContentTitle("Aviso BITTREXANALIZER")
                .setContentText(descricao.toString() + aviso + valor.toString())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        Notification not = builder.build();
        not.flags = Notification.FLAG_AUTO_CANCEL;

        Vibrator vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        long milliseconds = 500;
        vibrator.vibrate(milliseconds);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, not);

    }

    @Override
    public void run() {
        try {

            tickers = new LinkedList<>();
            tickers = tickerDAO.findAllTickers();

            if (tickers.size() == 0) {
                return;
            }

            ExecutorService executorService = Executors.newCachedThreadPool();
            for (Ticker t : tickers) {
                ticker = t;
                ticker.setUrlApi(WebServiceUtil.getUrl() + ticker.getSigla().toLowerCase());
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

            verificarAviso();

        } catch (Exception e) {
            Log.i(LOG, e.getMessage());
            emailUtil.enviarEmail(context, e.getMessage(), "ERRO");
        }
    }

    private synchronized void verificarAviso() {
        try {

            for (Ticker ticker : tickers) {

                descricao = new StringBuilder();
                descricao.append(ticker.getSigla());
                descricao.append(" ESTA COM O ");


                if (!(ticker.getAsk().compareTo(ticker.getAvisoBuyInferior()) == 1) &&
                        ticker.getAvisoBuyInferior().compareTo(BigDecimal.ZERO.setScale(8, RoundingMode.HALF_EVEN)) == 1) {
                    criarNotificacao(context, ticker.getAvisoBuyInferior(), "VALOR DE COMPRA ABAIXO DO ESPERADO ");
                }

                if (ticker.getAsk().compareTo(ticker.getAvisoBuySuperior()) == 1 &&
                        ticker.getAvisoBuySuperior().compareTo(BigDecimal.ZERO.setScale(8, RoundingMode.HALF_EVEN)) == 1) {
                    criarNotificacao(context, ticker.getAvisoBuySuperior(), "VALOR DE COMPRA ACIMA DO ESPERADO ");

                }

                if (!(ticker.getBid().compareTo(ticker.getAvisoStopLoss()) == 1) &&
                        ticker.getAvisoStopLoss().compareTo(BigDecimal.ZERO.setScale(8, RoundingMode.HALF_EVEN)) == 1) {
                    criarNotificacao(context, ticker.getAvisoStopLoss(), "VALOR DE VENDA ABAIXO DO ESPERADO ");
                }

                if (ticker.getBid().compareTo(ticker.getAvisoStopGain()) == 1 &&
                        ticker.getAvisoStopGain().compareTo(BigDecimal.ZERO.setScale(8, RoundingMode.HALF_EVEN)) == 1) {
                    criarNotificacao(context, ticker.getAvisoStopGain(), "VALOR DE VENDA ACIMA DO ESPERADO ");
                }
            }

            tickers = new LinkedList<>();

        } catch (Exception e) {
            Log.i(LOG, e.getMessage());
            emailUtil.enviarEmail(context, e.getMessage(), "ERRO");
        }
    }

}
