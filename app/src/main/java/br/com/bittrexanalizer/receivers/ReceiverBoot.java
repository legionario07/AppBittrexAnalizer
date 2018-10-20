package br.com.bittrexanalizer.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by PauLinHo on 09/02/2018.
 */

public class ReceiverBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        intent = new Intent(context, ServiceCompra.class);
        context.startService(intent);

        intent = new Intent(context, ServiceVenda.class);
        context.startService(intent);

    }
}
