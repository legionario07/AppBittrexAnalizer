package br.com.bittrexanalizer.strategy;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import br.com.bittrexanalizer.api.EncryptionUtility;
import br.com.bittrexanalizer.domain.Order;
import br.com.bittrexanalizer.utils.SessionUtil;
import br.com.bittrexanalizer.utils.WebServiceUtil;

/**
 * Created by PauLinHo on 04/02/2018.
 */

/**
 * Executa uma busca em todas as orderns abertas
 */
public class OpenOrderStrategy {

    private static boolean retorno = false;
    /**
     * Executo o método de busca
     *
     * @return - True se ocorreu sem erro
     *         - False se ocorreu algum erro
     */
    public static boolean execute() {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                LinkedList<Order> orders = new LinkedList<>();

                String url = WebServiceUtil.addNonce(WebServiceUtil.getUrlOpenOrders());

                if (url.length() < 1) {
                    return;
                }

                String hash = EncryptionUtility.calculateHash(url, EncryptionUtility.algorithmUsed);

                String dados = br.com.bittrexanalizer.webserver.HttpClient.find(url, hash);

                if (!WebServiceUtil.verificarRetorno(dados)) {
                    return;
                } else {
                    orders = new OrderStrategy().getObjects(dados);
                }

                if (orders.size() == 0) {
                    retorno = true;
                    return;
                }

                //atualiza a lista de ordens abertas
                Map<String, Order> mapOpenOrders = new HashMap<>();
                for (Order o : orders) {
                    mapOpenOrders.put(o.getSigla(), o);
                }


                SessionUtil.getInstance().setMapOpenOrders(mapOpenOrders);

                retorno = true;

            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return  retorno;

    }
}
