package br.com.bittrexanalizer.receivers;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import br.com.bittrexanalizer.facade.VendaFacade;

/**
 * Created by PauLinHo on 09/02/2018.
 */

public class ServiceVenda extends Service {
    private int startId;
    private boolean ativo = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startId = -1;
        if(ativo){
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

                    Context context = getBaseContext();

                    VendaFacade vendaFacade = new VendaFacade();
                    vendaFacade.executar(context);

                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        t.start();


        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        ativo = false;

        stopSelf(startId);

    }
}
