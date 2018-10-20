package br.com.bittrexanalizer.domain;

import android.support.annotation.NonNull;

import java.math.BigDecimal;

import br.com.bittrexanalizer.services.TickerService;

/**
 * Classe que representa as moedas que o sistema carrega assim que inicia
 * sera localiza os valores principais da moeda
 * isBought significa se foi comprada ou não
 */
public class Ticker extends TickerService implements Comparable<Ticker> {


    private Long id;
    private String nomeExchange;
    private String sigla;
    private String urlApi;
    /**
     * Indicação de Venda
     */
    private BigDecimal last;
    private BigDecimal bid;
    /**
     * Indicação de Compra
     */
    private BigDecimal ask;
    private Boolean isBought;

    private BigDecimal avisoBuyInferior;
    private BigDecimal avisoBuySuperior;
    private BigDecimal avisoStopLoss;
    private BigDecimal avisoStopGain;
    private BigDecimal valorDeCompra;


    public Ticker() {

        last = BigDecimal.ZERO;
        ask = BigDecimal.ZERO;
        bid = BigDecimal.ZERO;

        isBought = false;
        avisoBuyInferior = BigDecimal.ZERO;
        avisoBuySuperior = BigDecimal.ZERO;
        avisoStopLoss = BigDecimal.ZERO;
        avisoStopGain = BigDecimal.ZERO;
        setValorDeCompra(BigDecimal.ZERO);
        nomeExchange = "";
        sigla = "";


    }

    public String getNomeExchange() {
        return nomeExchange;
    }

    public void setNomeExchange(String nomeExchange) {
        this.nomeExchange = nomeExchange.toUpperCase();
    }

    public String getUrlApi() {
        return urlApi;
    }

    public void setUrlApi(String urlApi) {
        this.urlApi = urlApi;
    }

    public BigDecimal getLast() {
        return last;
    }

    public void setLast(BigDecimal last) {
        this.last = last;
    }

    public BigDecimal getBid() {
        return bid;
    }

    public void setBid(BigDecimal bid) {
        this.bid = bid;
    }

    public BigDecimal getAsk() {
        return ask;
    }

    public void setAsk(BigDecimal ask) {
        this.ask = ask;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla.toUpperCase();
    }

    public BigDecimal getAvisoBuyInferior() {
        return avisoBuyInferior;
    }

    public void setAvisoBuyInferior(BigDecimal avisoBuyInferior) {
        this.avisoBuyInferior = avisoBuyInferior;
    }

    public BigDecimal getAvisoBuySuperior() {
        return avisoBuySuperior;
    }

    public void setAvisoBuySuperior(BigDecimal avisoBuySuperior) {
        this.avisoBuySuperior = avisoBuySuperior;
    }

    public BigDecimal getAvisoStopLoss() {
        return avisoStopLoss;
    }

    public void setAvisoStopLoss(BigDecimal avisoStopLoss) {
        this.avisoStopLoss = avisoStopLoss;
    }

    public BigDecimal getAvisoStopGain() {
        return avisoStopGain;
    }

    public void setAvisoStopGain(BigDecimal avisoStopGain) {
        this.avisoStopGain = avisoStopGain;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ticker)) return false;

        Ticker ticker = (Ticker) o;

        return sigla.equals(ticker.sigla);

    }

    @Override
    public int hashCode() {
        return sigla.hashCode();
    }

    @Override
    public int compareTo(@NonNull Ticker ticker) {
        int value = this.getNomeExchange().compareTo(ticker.getNomeExchange());
        return value;
    }

    public Boolean getBought() {
        return isBought;
    }

    public void setBought(Boolean bought) {
        isBought = bought;
    }

    @Override
    public String toString() {
        return "Ticker{" +
                "id=" + id +
                ", nomeExchange='" + nomeExchange + '\'' +
                ", sigla='" + sigla + '\'' +
                ", urlApi='" + urlApi + '\'' +
                ", last=" + last +
                ", isBought=" + isBought +
                ", bid=" + bid +
                ", ask=" + ask +
                ", avisoBuyInferior=" + avisoBuyInferior +
                ", avisoBuySuperior=" + avisoBuySuperior +
                ", avisoStopLoss=" + avisoStopLoss +
                ", avisoStopGain=" + avisoStopGain +
                '}';
    }


    public BigDecimal getValorDeCompra() {
        return valorDeCompra;
    }

    public void setValorDeCompra(BigDecimal valorDeCompra) {
        this.valorDeCompra = valorDeCompra;
    }
}
