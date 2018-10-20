package br.com.bittrexanalizer.strategy;

import br.com.bittrexanalizer.api.EncryptionUtility;
import br.com.bittrexanalizer.domain.Order;
import br.com.bittrexanalizer.utils.WebServiceUtil;

/**
 * Created by PauLinHo on 31/01/2018.
 */

public class SellOrderStrategy {

    private static boolean hasCanceled = false;

    public static boolean execute(final Order order){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                //API_KEY&market=BTC-LTC&quantity=1.2&rate=


                String url = WebServiceUtil.construirURLTickerSellv1(order.getSigla(),
                        order.getQuantity().toString(),
                        order.getRate().toString());

                if (url.length() < 1) {
                    hasCanceled = false;
                    return;
                }

                String hash = EncryptionUtility.calculateHash(url, "HmacSHA512");

                String dados = br.com.bittrexanalizer.webserver.HttpClient.find(url, hash);

                if (!WebServiceUtil.verificarRetorno(dados)) {
                    hasCanceled = false;
                }else{
                    hasCanceled =true;
                }
            }
        });
        t.start();

        return hasCanceled;
    }
}
