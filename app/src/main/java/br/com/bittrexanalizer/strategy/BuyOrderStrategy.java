package br.com.bittrexanalizer.strategy;

import br.com.bittrexanalizer.api.EncryptionUtility;
import br.com.bittrexanalizer.domain.Order;
import br.com.bittrexanalizer.utils.WebServiceUtil;

/**
 * Created by PauLinHo on 31/01/2018.
 */

public class BuyOrderStrategy {

    private static boolean hasCanceled = false;

    public static boolean execute(final Order order){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                //API_KEY&market=BTC-LTC&quantity=1.2&rate=

//                MarketName:string, OrderType:string, Quantity:float, Rate:float,
//                TimeInEffect:string,ConditionType:string, Target:int __RequestVerificationToken:string

               // POST https://bittrex.com/api/v2.0/auth/market/TradeBuy with data { MarketName: "BTC-DGB,
                // OrderType:"LIMIT", Quantity: 10000.02, Rate: 0.0000004, TimeInEffect:"GOOD_TIL_CANCELED",
                // ConditionType: "NONE", Target: 0, __RequestVerificationToken: "HIDDEN_FOR_PRIVACY"}

                String url = WebServiceUtil.construirURLTickerBUYv1(order.getSigla(),
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
