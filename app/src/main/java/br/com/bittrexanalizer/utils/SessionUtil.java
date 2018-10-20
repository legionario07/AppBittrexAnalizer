package br.com.bittrexanalizer.utils;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import br.com.bittrexanalizer.api.ApiCredentials;
import br.com.bittrexanalizer.domain.Balance;
import br.com.bittrexanalizer.domain.Order;
import br.com.bittrexanalizer.domain.Ticker;

/**
 * Created by PauLinHo on 13/08/2017.
 */

public class SessionUtil {

    private static SessionUtil instance = new SessionUtil();
    private Ticker ticker;
    private LinkedList<Ticker> tickers;
    private ApiCredentials apiCredentials;
    private Map<String, String> mapConfiguracao;
    private Map<String, String> nomeExchanges;
    private long ultimoTempoDeNotificacaoSalvo = 0;
    private int maxCandleParaPesquisar = 0;
    private Map<String, Order> mapOpenOrders;
    private Map<String, Balance> mapBalances;
    private StringBuilder msgErros;

    private long ultimoHorarioSalvo;

    private SessionUtil() {
        setApiCredentials(new ApiCredentials());

        setMapConfiguracao(new HashMap<String, String>());
        setMapOpenOrders(new HashMap<String, Order>());
        setMapBalances(new HashMap<String, Balance>());

        nomeExchanges = new HashMap<String, String>();

        msgErros = new StringBuilder();

        ticker = new Ticker();
        tickers = new LinkedList<>();
    }

    public static SessionUtil getInstance() {
        return instance;
    }





    public Ticker getTicker() {
        return ticker;
    }

    public void setTicker(Ticker ticker) {
        this.ticker = ticker;
    }

    public LinkedList<Ticker> getTickers() {
        return tickers;
    }

    public void setTickers(LinkedList<Ticker> tickers) {
        this.tickers = tickers;
    }

    public ApiCredentials getApiCredentials() {
        return apiCredentials;
    }

    public void setApiCredentials(ApiCredentials apiCredentials) {
        this.apiCredentials = apiCredentials;
    }

    public Map<String, String> getMapConfiguracao() {
        return mapConfiguracao;
    }

    public void setMapConfiguracao(Map<String, String> mapConfiguracao) {
        this.mapConfiguracao = mapConfiguracao;
    }

    public Map<String, String> getNomeExchanges() {
        return nomeExchanges;
    }

    public void setNomeExchanges(Map<String, String> nomeExchanges) {
        this.nomeExchanges = nomeExchanges;
    }

    public long getUltimoTempoDeNotificacaoSalvo() {
        return ultimoTempoDeNotificacaoSalvo;
    }

    public void setUltimoTempoDeNotificacaoSalvo(long ultimoTempoDeNotificacaoSalvo) {
        this.ultimoTempoDeNotificacaoSalvo = ultimoTempoDeNotificacaoSalvo;
    }

    public int getMaxCandleParaPesquisar() {
        return maxCandleParaPesquisar;
    }

    public void setMaxCandleParaPesquisar(int maxCandleParaPesquisar) {
        this.maxCandleParaPesquisar = maxCandleParaPesquisar;
    }

    public Map<String, Order> getMapOpenOrders() {
        return mapOpenOrders;
    }

    public void setMapOpenOrders(Map<String, Order> mapOpenOrders) {
        this.mapOpenOrders = mapOpenOrders;
    }

    public Map<String, Balance> getMapBalances() {
        return mapBalances;
    }

    public void setMapBalances(Map<String, Balance> mapBalances) {
        this.mapBalances = mapBalances;
    }

    public StringBuilder getMsgErros() {
        return msgErros;
    }

    public void setMsgErros(StringBuilder msgErros) {
        this.msgErros = msgErros;
    }

    public long getUltimoHorarioSalvo() {
        return ultimoHorarioSalvo;
    }

    public void setUltimoHorarioSalvo(long ultimoHorarioSalvo) {
        this.ultimoHorarioSalvo = ultimoHorarioSalvo;
    }
}
