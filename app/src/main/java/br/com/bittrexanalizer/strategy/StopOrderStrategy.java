package br.com.bittrexanalizer.strategy;

import br.com.bittrexanalizer.api.EncryptionUtility;
import br.com.bittrexanalizer.domain.Order;
import br.com.bittrexanalizer.domain.OrderHistory;
import br.com.bittrexanalizer.domain.data;
import br.com.bittrexanalizer.utils.WebServiceUtil;
import br.com.bittrexanalizer.webserver.HttpClient;

/**
 * Created by PauLinHo on 31/01/2018.
 */

public class StopOrderStrategy {

    private static boolean hasCanceled = false;

    public static boolean execute(final Order order) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                // POST https://bittrex.com/api/v2.0/auth/market/TradeBuy with data { MarketName: "BTC-DGB,
                // OrderType:"LIMIT", Quantity: 10000.02, Rate: 0.0000004, TimeInEffect:"GOOD_TIL_CANCELED",
                // ConditionType: "NONE", Target: 0, __RequestVerificationToken: "HIDDEN_FOR_PRIVACY"}

    /*MarketName:string, OrderType:string, Quantity:float,
    Rate:float, TimeInEffect:string,ConditionType:string,
    Target:int __RequestVerificationToken:string*/

                String url = WebServiceUtil.constuirgetUrlOrderHistoryV2_0();
                OrderHistory orderHistory = new OrderHistory();

                String hash = EncryptionUtility.calculateHash(url, EncryptionUtility.algorithmUsed);
                orderHistory.set_RequestVerificationToken(hash);

                data data = new data();
                data.set__RequestVerificationToken(EncryptionUtility.generateNonce());



                String dados = HttpClient.findPost(WebServiceUtil.getUrlOrderHistoryV20(),hash, data);



                if (url.length() < 1) {
                    hasCanceled = false;
                    return;
                }


//                String dados = br.com.bittrexanalizer.webserver.HttpClient.find(url, hash);

            }
        });
        t.start();

        return hasCanceled;
    }
}
