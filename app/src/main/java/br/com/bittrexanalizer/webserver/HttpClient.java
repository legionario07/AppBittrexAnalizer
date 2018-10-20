package br.com.bittrexanalizer.webserver;

import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import br.com.bittrexanalizer.domain.MarketHistory;
import br.com.bittrexanalizer.domain.Ticker;

/**
 * Created by PauLinHo on 10/09/2017.
 */

public class HttpClient {

    private static Gson gson = null;
    private static StringBuilder retorno = null;

    private static volatile Ticker ticker = null;
    private static MarketHistory marketHistory = null;
    private static final String urlAllCurrencies = "https://bittrex.com/api/v1.1/public/getcurrencies";
    private static final String GET_MARTKET_SUMMARY = "https://bittrex.com/api/v1.1/public/getmarketsummary?market=btc-";
    private static final String GET_MARKET_HISTORY = "https://bittrex.com/api/v1.1/public/getmarkethistory?market=btc-";


    /**
     * @param enderecoURL - um endereço URL
     * @return - Um String Gson com a entidade encontrada ou NULL
     */
    public synchronized static Ticker find(String enderecoURL) {

        String output = null;

        ticker = new Ticker();

        StringBuffer temp = new StringBuffer();
        temp.append(enderecoURL);

        try {
            URL url = new URL(temp.toString());

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : Http error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            retorno = new StringBuilder();

            while ((output = br.readLine()) != null) {
                retorno.append(output);
            }

            ticker = getTicker(retorno.toString());

            conn.disconnect();

        } catch (Exception e) {
            Log.d("ERRO", e.getMessage());
            return null;
        }

        return ticker;
    }

    public static Map<String, String> findAllCurrencies() {

        String output = null;

        Map<String, String> mapString = new HashMap<>();

        StringBuffer temp = new StringBuffer();
        temp.append(urlAllCurrencies);

        try {
            URL url = new URL(temp.toString());

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : Http error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            retorno = new StringBuilder();

            while ((output = br.readLine()) != null) {
                retorno.append(output);
            }

            mapString = getAllCurrencies(retorno.toString());

            conn.disconnect();

        } catch (Exception e) {
            Log.d("ERRO", e.getMessage());
            return null;
        }

        return mapString;
    }


    private static synchronized Ticker getTicker(String dados) {

        DecimalFormat df = new DecimalFormat("#.########");

        String[] temp = dados.split("\\{");
        String[] temp2 = temp[2].split(",");
        for (int i = 0; i < temp2.length; i++) {
            temp2[i] = temp2[i].replace("\"", "");
            temp2[i] = temp2[i].replace("{", "");
            temp2[i] = temp2[i].replace("}", "");
            String str[] = temp2[i].split(":");


            switch (str[0].trim()) {
                case "Bid":
                    ticker.setBid(new BigDecimal(str[1].trim()));
                    break;
                case "Ask":
                    ticker.setAsk(new BigDecimal(str[1].trim()));
                    break;
                case "Last":
                    ticker.setLast(new BigDecimal(str[1].trim()));
                    break;
            }


        }
        return ticker;
    }

    private static Map<String, String> getAllCurrencies(String dados) {

        Map<String, String> mapRetorno = new HashMap<>();
        String key = "";
        String valor = "";

        String[] temp = dados.split("\\{");
        for (int j = 2; j < temp.length; j++) {
            String[] temp2 = temp[j].split(",");
            for (int i = 0; i < temp2.length; i++) {
                temp2[i] = temp2[i].replace("\"", "");
                temp2[i] = temp2[i].replace("{", "");
                temp2[i] = temp2[i].replace("}", "");
                String str[] = temp2[i].split(":");

                switch (str[0].trim()) {
                    case "Currency":
                        key = str[1].trim();
                        break;
                    case "CurrencyLong":
                        mapRetorno.put(key, str[1].trim());
                        break;
                    case "IsActive":
                        //moeda não esta ativa
                        if (str[1].toLowerCase().equals("false")) {
                            mapRetorno.remove(key);
                        }
                        break;
                }
            }


        }
        return mapRetorno;
    }

    public static Ticker getMarketSummary(String siglaMoeda) {

        String output = null;

        ticker = new Ticker();

        StringBuffer temp = new StringBuffer();
        temp.append(GET_MARTKET_SUMMARY);
        temp.append(siglaMoeda);

        try {
            URL url = new URL(temp.toString());

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : Http error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            retorno = new StringBuilder();

            while ((output = br.readLine()) != null) {
                retorno.append(output);
            }

            ticker = getTickerFull(retorno.toString());

            conn.disconnect();

        } catch (Exception e) {
            Log.d("ERRO", e.getMessage());
            return null;
        }

        return ticker;
    }

    public static MarketHistory getMarketHistory(String siglaMoeda) {

        String output = null;

        marketHistory = new MarketHistory();

        StringBuffer temp = new StringBuffer();
        temp.append(GET_MARKET_HISTORY);
        temp.append(siglaMoeda);

        try {
            URL url = new URL(temp.toString());

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : Http error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            retorno = new StringBuilder();

            while ((output = br.readLine()) != null) {
                retorno.append(output);
            }

            marketHistory = getMarketHistorySplit(retorno.toString());

            conn.disconnect();

        } catch (Exception e) {
            Log.d("ERRO", e.getMessage());
            return null;
        }

        return marketHistory;
    }


    private static Ticker getTickerFull(String dados) {

        DecimalFormat df = new DecimalFormat("#.########");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

        String[] temp = dados.split("\\{");
        String[] temp2 = temp[2].split(",");
        for (int i = 0; i < temp2.length; i++) {
            temp2[i] = temp2[i].replace("\"", "");
            temp2[i] = temp2[i].replace("{", "");
            temp2[i] = temp2[i].replace("}", "");
            String str[] = temp2[i].split(":");

            switch (str[0].trim()) {
                case "Bid":
                    ticker.setBid(new BigDecimal(str[1].trim()));
                    break;
                case "Ask":
                    ticker.setAsk(new BigDecimal(str[1].trim()));
                    break;
                case "Last":
                    ticker.setLast(new BigDecimal(str[1].trim()));
                    break;

            }


        }
        return ticker;
    }

    private static MarketHistory getMarketHistorySplit(String dados) {

        DecimalFormat df = new DecimalFormat("#.########");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        String[] temp = dados.split("\\{");
        String[] temp2 = temp[2].split(",");
        for (int i = 0; i < temp2.length; i++) {
            temp2[i] = temp2[i].replace("\"", "");
            temp2[i] = temp2[i].replace("{", "");
            temp2[i] = temp2[i].replace("}", "");
            String str[] = temp2[i].split(":");

            /*"Id" : 319435,
                    "TimeStamp" : "2014-07-09T03:21:20.08",
                    "Quantity" : 0.30802438,
                    "Price" : 0.01263400,
                    "Total" : 0.00389158,
                    "FillType" : "FILL",
                    "OrderType" : "BUY"*/

            switch (str[0].trim()) {
                case "Id":
                    marketHistory.setId(Long.valueOf(str[1].trim()));
                    break;
                case "Quantity":
                    marketHistory.setQuantity(new BigDecimal(str[1].trim()));
                    break;
                case "Price":
                    marketHistory.setPrice(new BigDecimal(str[1].trim()));
                    break;
                case "Total":
                    marketHistory.setTotal(new BigDecimal(str[1].trim()));
                    break;
                case "FillType":
                    marketHistory.setFillType(str[1].trim());
                    break;
                case "OrderType":
                    marketHistory.setOrderType(str[1].trim());
                    break;
                case "TimeStamp":
                    try {
                        StringBuilder data = new StringBuilder();
                        data.append((str[1].trim().replace("T", " ")));
                        data.append(":");
                        data.append(str[2].trim());
                        data.append(":");
                        String dataSplit[] = str[3].trim().replace(".", ":").split(":");
                        data.append(dataSplit[0]);
                        marketHistory.setTimeStamp(sdf.parse(data.toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;

            }


        }
        return marketHistory;
    }

    public static String find(String enderecoURL, String API_KEY) {

        String output = null;

        StringBuffer resultBuffer;

        StringBuffer temp = new StringBuffer();
        temp.append(enderecoURL);

        org.apache.http.client.HttpClient client = new DefaultHttpClient();
        //CloseableHttpClient client = HttpClientBuilder.create().build();

        try {
            URL url = new URL(temp.toString());

            HttpGet httpGet = new HttpGet();
            if (API_KEY != null) {
                httpGet.addHeader("apisign", API_KEY); // Attaches signature as a header
            }
            httpGet.setURI(new URI(enderecoURL));

            HttpResponse response = client.execute(httpGet);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            resultBuffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null)

                resultBuffer.append(line);


        } catch (Exception e) {
            Log.i("BITTREX", e.getMessage());
            return null;
        }

        return resultBuffer.toString();
    }

    public static String findPost(String enderecoURL, String API_KEY, Object dados) {

        String output = null;

        StringBuffer resultBuffer;

        StringBuffer temp = new StringBuffer();
        temp.append(enderecoURL);

        org.apache.http.client.HttpClient client = new DefaultHttpClient();

        try {
            URL url = new URL(temp.toString());

            HttpPost httpPost = new HttpPost();
            if (API_KEY != null) {
                httpPost.addHeader("apisign", API_KEY); // Attaches signature as a header
           }
           //httpPost.addHeader("__RequestVerificationToken", "LgflegHnn0I-ubbRaB8J0IaWj3w9NzhpqqiEOJeQUUiZfvXwH-A26WJ0oqP8u3tGbT8LVv7ZPSEYviISqUNlc67LCB2gpXWG5u-doPXBTEBiXPs0bEJYQ2F8YKlFkLGnSFkv6A2");
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setURI(new URI(enderecoURL));

            Gson gson = new Gson();
            String data = gson.toJson(dados);
//
            Log.i("Bitttrex", data);
//            HttpEntity entity = new StringEntity(data);
//            httpPost.setEntity(entity);

            HttpResponse response = client.execute(httpPost);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            resultBuffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null)

                resultBuffer.append(line);


        } catch (Exception e) {
            Log.i("BITTREX", e.getMessage());
            return null;
        }

        return resultBuffer.toString();
    }

}
