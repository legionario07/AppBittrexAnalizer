package br.com.bittrexanalizer.utils;

import br.com.bittrexanalizer.api.EncryptionUtility;
import br.com.bittrexanalizer.domain.OrderHistory;

/**
 * Created by PauLinHo on 10/09/2017.
 */

public class WebServiceUtil {

    private static final String URL = "https://bittrex.com/api/v1.1/public/getticker?market=btc-";
    private static final String URL_BALANCE_APY_KEY = "https://bittrex.com/api/v1.1/account/getbalances?apikey=";
    private static final String URL_OPEN_ORDERS = "https://bittrex.com/api/v1.1/market/getopenorders?apikey=";
    private static final String URL_ORDER = "https://bittrex.com/api/v1.1/account/getorder&uuid=";
    private static final String URL_TICKS = "https://bittrex.com/Api/v2.0/pub/market/GetTicks?marketName=BTC-";
    private static final String URL_ORDER_HISTORY = "https://bittrex.com/api/v1.1/account/getorderhistory";

    private static final String URL_TRADE_BUY_V1 = "https://bittrex.com/api/v1.1/market/buylimit?apikey=";
    private static final String URL_TRADE_SELL_V1 = "https://bittrex.com/api/v1.1/market/selllimit?apikey=";
    private static final String URL_ORDER_CANCEL = "https://bittrex.com/api/v1.1/market/cancel?apikey=";

    //V2.0
    private static final String URL_TRADE_BUY = "https://bittrex.com/api/v2.0/auth/market/TradeBuy";
    private static final String URL_TRADE_SELL = "https://bittrex.com/api/v2.0/auth/market/TradSell";
    private static final String URL_ORDER_HISTORY_V_20 = "https://bittrex.com/Api/v2.0/auth/orders/GetOrderHistory";

    private static final String TICKINTERVAL_ONE_MIN = "oneMin";
    private static final String TICKINTERVAL_FIVE_MIN = "fiveMin";
    private static final String TICKINTERVAL_THIRTY_MIN = "thirtyMin";
    private static final String TICKINTERVAL_HOUR = "hour";
    private static final String TICKINTERVAL_DAY = "day";


    public static String getUrl() {
        return getURL();
    }

    /**
     * Verifica se a dados devolvido do web service foi true ou false
     *
     * @param dados
     * @return
     */
    public static boolean verificarRetorno(String dados) {
        boolean retorno = false;

        String[] split = dados.replace("{", "")
                .replace("}","")
                .replace(":","")
                .replace(",","")
                .split("\"");

        for(int i = 0; i<split.length-1;i++){
            if(split[i].equals("success")){
                retorno = new Boolean(split[i+1]);
                break;
            }
        }

        return retorno;

    }

    /**
     * Ex. https://bittrex.com/Api/v2.0/pub/market/GetTicks?marketName=BTC-CVC&tickInterval=thirtyMin&_=1500915289433
     *
     * @param URL               URL BASE
     * @param marketName:string - SIGLA DA MOEDA
     * @param tickInterval      [“oneMin”, “fiveMin”, “thirtyMin”, “hour”, “day”]
     * @return
     */
    public static String construirURLTicker(String URL, String marketName, String tickInterval) {


        StringBuilder uri = new StringBuilder();

        uri.append(URL);
        uri.append(marketName);
        uri.append("&tickInterval=");
        uri.append(tickInterval);
        uri.append("&_=");
        uri.append(System.currentTimeMillis());


        return uri.toString();

    }

    public static String construirURLTickerBUYv1(String marketName, String quantity, String rate) {

        //API_KEY&market=BTC-LTC&quantity=1.2&rate=1.3";

        StringBuilder uri = new StringBuilder();

        uri.append(getUrlTradeBuyV1());
        uri.append(SessionUtil.getInstance().getApiCredentials().getKey());
        uri.append("&");
        uri.append("nonce");
        uri.append("=");
        uri.append(EncryptionUtility.generateNonce());
        uri.append("&market=BTC-");
        uri.append(marketName);
        uri.append("&quantity=");
        uri.append(quantity);
        uri.append("&rate=");
        uri.append(rate);


        return uri.toString();

    }

    public static String construirURLTickerSellv1(String marketName, String quantity, String rate) {

        //API_KEY&market=BTC-LTC&quantity=1.2&rate=1.3";

        StringBuilder uri = new StringBuilder();

        uri.append(getUrlTradeSellV1());
        uri.append(SessionUtil.getInstance().getApiCredentials().getKey());
        uri.append("&");
        uri.append("nonce");
        uri.append("=");
        uri.append(EncryptionUtility.generateNonce());
        uri.append("&market=BTC-");
        uri.append(marketName);
        uri.append("&quantity=");
        uri.append(quantity);
        uri.append("&rate=");
        uri.append(rate);


        return uri.toString();

    }


    public static String constuirgetUrlOrderHistoryV2_0() {

        //                MarketName:string, OrderType:string, Quantity:float, Rate:float,
//                TimeInEffect:string,ConditionType:string, Target:int __RequestVerificationToken:string

        // POST https://bittrex.com/api/v2.0/auth/market/TradeBuy with data { MarketName: "BTC-DGB,
        // OrderType:"LIMIT", Quantity: 10000.02, Rate: 0.0000004, TimeInEffect:"GOOD_TIL_CANCELED",
        // ConditionType: "NONE", Target: 0, __RequestVerificationToken: "HIDDEN_FOR_PRIVACY"}

        OrderHistory orderHistory = new OrderHistory();
        orderHistory.set_RequestVerificationToken(SessionUtil.getInstance().getApiCredentials().getKey());

        StringBuilder uri = new StringBuilder();
        uri.append(getUrlOrderHistoryV20());
        uri.append("?apikey=");
        uri.append(SessionUtil.getInstance().getApiCredentials().getKey());
        uri.append("&");
        uri.append("nonce");
        uri.append("=");
        uri.append(EncryptionUtility.generateNonce());

//        uri.append("&market=BTC-");
//        uri.append(order.getExchange());
//        uri.append("&quantity=");
//        uri.append(order.getQuantity());
//        uri.append("&OrderType=");
//        uri.append(order.getOrderType());
//        uri.append("&rate=");
//        uri.append(order.getRate());


        return uri.toString();

    }



    public static String addNonce(String URL) {

        if (SessionUtil.getInstance().getApiCredentials() == null) {
            return "";
        }

        StringBuilder uri = new StringBuilder();

        uri.append(URL);
        uri.append(SessionUtil.getInstance().getApiCredentials().getKey());
        uri.append("&");
        uri.append("nonce");
        uri.append("=");
        uri.append(EncryptionUtility.generateNonce());


        return uri.toString();

    }

    public static String addNonce(String URL, String UUID) {

        if (SessionUtil.getInstance().getApiCredentials() == null) {
            return "";
        }

        StringBuilder uri = new StringBuilder();

        uri.append(URL);
        uri.append(SessionUtil.getInstance().getApiCredentials().getKey());
        uri.append("&");
        uri.append("nonce");
        uri.append("=");
        uri.append(EncryptionUtility.generateNonce());
        uri.append("&uuid=");
        uri.append(UUID);


        return uri.toString();

    }


    public static String getURL() {
        return URL;
    }

    public static String getUrlBalanceApyKey() {
        return URL_BALANCE_APY_KEY;
    }

    public static String getUrlOpenOrders() {
        return URL_OPEN_ORDERS;
    }

    public static String getUrlOrder() {
        return URL_ORDER;
    }

    public static String getUrlTicks() {
        return URL_TICKS;
    }

    public static String getTickintervalOneMin() {
        return TICKINTERVAL_ONE_MIN;
    }

    public static String getTickintervalFiveMin() {
        return TICKINTERVAL_FIVE_MIN;
    }

    public static String getTickintervalThirtyMin() {
        return TICKINTERVAL_THIRTY_MIN;
    }

    public static String getTickintervalHour() {
        return TICKINTERVAL_HOUR;
    }

    public static String getTickintervalDay() {
        return TICKINTERVAL_DAY;
    }

    public static String getUrlOrderHistory() {
        return URL_ORDER_HISTORY;
    }

    public static String getUrlTradeBuyV1() {
        return URL_TRADE_BUY_V1;
    }

    /**
     * { MarketName: "BTC-DGB,
     * OrderType:"LIMIT",
     * Quantity: 10000.02,
     * Rate: 0.0000004,
     * TimeInEffect:"GOOD_TIL_CANCELED",
     * ConditionType: "NONE",
     * Target: 0,
     * __RequestVerificationToken:
     * "HIDDEN_FOR_PRIVACY"}
     */
    public static String getUrlTradeBuy() {
        return URL_TRADE_BUY;
    }

    /**
     * { MarketName: "BTC-DGB,
     * OrderType:"LIMIT",
     * Quantity: 10000.02,
     * Rate: 0.0000004,
     * TimeInEffect:"GOOD_TIL_CANCELED",
     * ConditionType: "NONE",
     * Target: 0,
     * __RequestVerificationToken: "HIDDEN_FOR_PRIVACY"}
     */
    public static String getUrlTradeSell() {
        return URL_TRADE_SELL;
    }

    public static String getUrlOrderCancel() {
        return URL_ORDER_CANCEL;
    }

    public static String getUrlTradeSellV1() {
        return URL_TRADE_SELL_V1;
    }

    public static String getUrlOrderHistoryV20() {
        return URL_ORDER_HISTORY_V_20;
    }
}