package br.com.bittrexanalizer.receivers;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import br.com.bittrexanalizer.facade.CompraFacade;
import br.com.bittrexanalizer.utils.ConstantesUtil;
import br.com.bittrexanalizer.utils.EmailUtil;
import br.com.bittrexanalizer.utils.SessionUtil;

/**
 * Created by PauLinHo on 09/02/2018.
 */

public class ServiceCompra extends Service {

    private int startId;
    public static boolean ativo = false;
    private Context context;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startId = -1;
        if (ativo) {
            return;
        }
        ativo = true;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        this.startId = startId;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                while (ativo) {

                    try {
                        Thread.sleep(660000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    context = getBaseContext();

                    CompraFacade compraFacade = new CompraFacade();
                    compraFacade.executar(context);

                    long tempoEnvioEmail = Long.valueOf(SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.ROBOT_TEMPO_ENVIO_EMAIL));
                    if (System.currentTimeMillis() >= (SessionUtil.getInstance().getUltimoHorarioSalvo() + tempoEnvioEmail)) {
                        enviarEmail();
                    }


                }
            }
        });
        t.start();


        return START_STICKY;
    }

    private void enviarEmail() {

        EmailUtil emailUtil = new EmailUtil();

        emailUtil.enviarEmail(context, SessionUtil.getInstance().getMsgErros().toString(), "ROBOT ERROS");

        SessionUtil.getInstance().setMsgErros(new StringBuilder());
        SessionUtil.getInstance().setUltimoHorarioSalvo(System.currentTimeMillis());


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopSelf(startId);
        ativo = false;

    }
}
