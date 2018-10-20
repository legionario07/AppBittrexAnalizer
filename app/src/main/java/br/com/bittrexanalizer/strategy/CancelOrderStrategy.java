package br.com.bittrexanalizer.strategy;

import br.com.bittrexanalizer.api.EncryptionUtility;
import br.com.bittrexanalizer.domain.Order;
import br.com.bittrexanalizer.utils.WebServiceUtil;

/**
 * Created by PauLinHo on 31/01/2018.
 */

public class CancelOrderStrategy {

    private static boolean hasCanceled = false;

    public static boolean execute(final Order order){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                String url = WebServiceUtil.addNonce(WebServiceUtil.getUrlOrderCancel(), order.getOrderUuid());


                if (url.length() < 1) {
                    hasCanceled = false;
                    return;
                }

                String hash = EncryptionUtility.calculateHash(url, EncryptionUtility.algorithmUsed);

                String dados = br.com.bittrexanalizer.webserver.HttpClient.find(url, hash);


                if (!WebServiceUtil.verificarRetorno(dados)) {
                    hasCanceled = false;
                }else{
                    hasCanceled =true;
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return hasCanceled;
    }
}
