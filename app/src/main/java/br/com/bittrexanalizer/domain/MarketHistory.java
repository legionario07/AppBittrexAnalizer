package br.com.bittrexanalizer.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by PauLinHo on 11/01/2018.
 */

public class MarketHistory {

    /*[{
        "Id" : 319435,
                "TimeStamp" : "2014-07-09T03:21:20.08",
                "Quantity" : 0.30802438,
                "Price" : 0.01263400,
                "Total" : 0.00389158,
                "FillType" : "FILL",
                "OrderType" : "BUY"*/

    private Long id;
    private Ticker ticker;
    private Date timeStamp;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal total;
    private String fillType;
    private String orderType;

    public MarketHistory(){

        timeStamp = new Date();
        ticker = new Ticker();
    }

    public MarketHistory(Long id, Date timeStamp, BigDecimal quantity,
                         BigDecimal price, BigDecimal total, String fillType, String orderType) {
        this.id = id;
        this.timeStamp = timeStamp;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
        this.fillType = fillType;
        this.orderType = orderType;
    }

    public MarketHistory(Long id, Ticker ticker, Date timeStamp, BigDecimal quantity,
                         BigDecimal price, BigDecimal total, String fillType, String orderType) {
        this.id = id;
        this.ticker = ticker;
        this.timeStamp = timeStamp;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
        this.fillType = fillType;
        this.orderType = orderType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ticker getTicker() {
        return ticker;
    }

    public void setTicker(Ticker ticker) {
        this.ticker = ticker;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getFillType() {
        return fillType;
    }

    public void setFillType(String fillType) {
        this.fillType = fillType;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    @Override
    public String toString() {
        return "MarketHistory{" +
                "id=" + id +
                ", ticker=" + ticker +
                ", timeStamp=" + timeStamp +
                ", quantity=" + quantity +
                ", price=" + price +
                ", total=" + total +
                ", fillType='" + fillType + '\'' +
                ", orderType='" + orderType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MarketHistory)) return false;

        MarketHistory that = (MarketHistory) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
