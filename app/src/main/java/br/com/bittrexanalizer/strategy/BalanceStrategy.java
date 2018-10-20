package br.com.bittrexanalizer.strategy;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import br.com.bittrexanalizer.api.EncryptionUtility;
import br.com.bittrexanalizer.domain.Balance;
import br.com.bittrexanalizer.utils.SessionUtil;
import br.com.bittrexanalizer.utils.WebServiceUtil;

/**
 * Created by PauLinHo on 17/01/2018.
 */

public class BalanceStrategy implements IStrategy<Balance> {

    private LinkedList<Balance> objetos;

    private static boolean retorno = false;


    /**
     * Executo o método de busca
     *
     * @return - True se ocorreu sem erro
     * - False se ocorreu algum erro
     */
    public static boolean execute() {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {


                LinkedList<Balance> balances = new LinkedList<>();

                String url = WebServiceUtil.addNonce(WebServiceUtil.getUrlBalanceApyKey());

                if (url.length() < 1) {
                    return;
                }

                String hash = EncryptionUtility.calculateHash(url, EncryptionUtility.algorithmUsed);

                String dados = br.com.bittrexanalizer.webserver.HttpClient.find(url, hash);

                if (dados == null) {
                    return;
                }
                if (!WebServiceUtil.verificarRetorno(dados)) {
                    return;
                } else {
                    balances = new BalanceStrategy().getObjects(dados);

                }
                if (balances.size() == 0) {
                    retorno = true;
                    return;
                }

                retorno = true;

            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return retorno;
    }


    @Override
    public LinkedList<Balance> getObjects(String dados) {

        objetos = new LinkedList<>();

        Map<String, Balance> mapBalances = new HashMap<>();

        dados = dados.replace("}", "");

        String[] dadosTemp = dados.split("\\{");

        for (int i = 2; i < dadosTemp.length; i++) {
            Balance balance = getBalances(dadosTemp[i]);

            if (balance.getAvailable().compareTo(BigDecimal.ZERO) == 1 ||
                    balance.getBalance().compareTo(BigDecimal.ZERO) == 1) {
                objetos.add(balance);
                mapBalances.put(balance.getCurrency(), balance);
            }

        }

        SessionUtil.getInstance().setMapBalances(mapBalances);

        return objetos;
    }

    private synchronized static Balance getBalances(String dados) {

        Balance balance = new Balance();

        /*"Currency" : "DOGE",
    "Balance" : 0.00000000,
	"Available" : 0.00000000,
	"Pending" : 0.00000000,
	"CryptoAddress" : "DLxcEt3AatMyr2NTatzjsfHNoB9NT62HiF",
	"Requested" : false,
	"Uuid" : null*/

        String[] dadosTemp = dados.split(",");

        for (String s : dadosTemp) {

            String key[] = s.replace("]", "").split(":");

            switch (key[0].replace("\"", "").replace("\"", "")) {
                case "Currency":
                    balance.setCurrency(key[1].replace("\"", ""));

                    break;
                case "Balance":
                    if (key[1].equals("0.00000000"))
                        balance.setBalance(new BigDecimal("0.0"));
                    else
                        balance.setBalance(new BigDecimal(key[1]));
                    break;
                case "Available":
                    if (key[1].equals("0.00000000"))
                        balance.setAvailable(new BigDecimal("0.0"));
                    else
                        balance.setAvailable(new BigDecimal(key[1].replace("\"", "")));
                    break;
                case "Pending":
                    if (key[1].equals("0.00000000"))
                        balance.setPending(new BigDecimal("0.0"));
                    else
                        balance.setPending(new BigDecimal(key[1].replace("\"", "")));
                    break;
                case "CryptoAddress":
                    if (key[1].toLowerCase().replace("\"", "").equals("null"))
                        balance.setCryptoAddress("");
                    else
                        balance.setCryptoAddress(key[1].replace("\"", ""));
                    break;
                case "Request":
                    if (key[1].toLowerCase().replace("\"", "").equals("null"))
                        balance.setRequested(false);
                    else
                        balance.setRequested(Boolean.valueOf(key[1].replace("\"", "")));
                    break;
                case "Uuid":
                    if (key[1].toLowerCase().replace("\"", "").equals("null"))
                        balance.setUuid("");
                    else
                        balance.setUuid(key[1].replace("\"", ""));
                    break;
            }

        }

        return balance;
    }
}
