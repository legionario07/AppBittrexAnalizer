package br.com.bittrexanalizer.domain;

import java.math.BigDecimal;
import java.util.Calendar;

public class Order extends EntidadeDomain implements Comparable<Order> {

/*
	AccountId" : null,
	"Uuid" : null,
	"OrderUuid" : "09aa5bb6-8232-41aa-9b78-a5a1093e0211",
	"Exchange" : "BTC-LTC",
	"Type" : "LIMIT_BUY",
	"OrderType" : "LIMIT_SELL",
	"Quantity" : 5.00000000,
	"QuantityRemaining" : 5.00000000,
	"Limit" : 2.00000000,
	"Reserved" : 0.00001000,
	"ReserveRemaining" : 0.00001000,
	"CommissionPaid" : 0.00000000,
	"CommissionReserved" : 0.00000002,
	"CommissionReserveRemaining" : 0.00000002,
	"Price" : 0.00000000,
	"PricePerUnit" : null,
	"Opened" : "2014-07-09T03:55:48.77",
	"Closed" : null,
	"IsOpen" : true,
	"CancelInitiated" : false,
	"ImmediateOrCancel" : false,
	"IsConditional" : false,
	"Condition" : null,
	"Sentinel" : "6c454604-22e2-4fb4-892e-179eede20972",
	"ConditionTarget" : null*/


    /*Closed: "2017-07-23T21:01:04.65",
    Commission: 0.00024937,
    Condition: "NONE",
    ConditionTarget: null,
    Exchange: "BTC-RDD",
    ImmediateOrCancel: false,
    IsConditional: false,
    Limit: 4.8e-7,
    OrderType: "LIMIT_BUY",
    OrderUuid: "44e8751c-3df8-4a75-841b-c7c2145b746b",
    Price: 0.09975,
    PricePerUnit: 4.8e-7,
    Quantity: 207812.5,
    QuantityRemaining: 0,
    TimeStamp: "2017-07-23T18:17:40.387"*/

    private Long id;
    private Long AccountId;
    private String uuid;
    private String orderUuid;
    private String exchange;
    private String sigla;
    private String type;
    private String orderType;
    private BigDecimal quantity;
    private BigDecimal quantityRemaining;
    private BigDecimal rate;
    private BigDecimal reserved;
    private BigDecimal reserveRemaining;
    private BigDecimal limit;
    private BigDecimal comission;
    private BigDecimal comissionPaid;
    private BigDecimal comissionReserved;
    private BigDecimal comissionReservedRemaining;
    private BigDecimal price;
    private BigDecimal pricePerUnit;
    private Calendar timeStamp;
    private Calendar opened;
    private Calendar closed;
    private Boolean isOpen;
    private Boolean cancelInitiated;
    private Boolean immediateOrCancel;
    private Boolean isConditional;
    private String sentinel;
    private String condition;
    private String conditionTarget;
    private String conditionType;
    private String timeInEffect;

    public Order() {
        quantity = BigDecimal.ZERO;
        quantityRemaining = BigDecimal.ZERO;
        limit = BigDecimal.ZERO;
        setComissionPaid(BigDecimal.ZERO);
        comission = BigDecimal.ZERO;
        comissionReserved = BigDecimal.ZERO;
        comissionReservedRemaining = BigDecimal.ZERO;
        reserved = BigDecimal.ZERO;
        reserveRemaining = BigDecimal.ZERO;
        price = BigDecimal.ZERO;
        pricePerUnit = BigDecimal.ZERO;
        setRate(BigDecimal.ZERO);
        timeStamp = Calendar.getInstance();
        opened = Calendar.getInstance();
        closed = Calendar.getInstance();
    }


    public String getUuid() {
        return uuid;
    }


    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public String getOrderUuid() {
        return orderUuid;
    }


    public void setOrderUuid(String orderUuid) {
        this.orderUuid = orderUuid;
    }


    public String getExchange() {
        return exchange;
    }


    public void setExchange(String exchange) {
        this.exchange = exchange;
    }


    public String getOrderType() {
        return orderType;
    }


    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }


    public BigDecimal getQuantity() {
        return quantity;
    }


    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }


    public BigDecimal getQuantityRemaining() {
        return quantityRemaining;
    }


    public void setQuantityRemaining(BigDecimal quantityRemaining) {
        this.quantityRemaining = quantityRemaining;
    }


    public BigDecimal getLimit() {
        return limit;
    }


    public void setLimit(BigDecimal limit) {
        this.limit = limit;
    }


    public BigDecimal getComissionPaid() {
        return comissionPaid;
    }


    public void setComissionPaid(BigDecimal comissionPaid) {
        this.comissionPaid = comissionPaid;
    }


    public BigDecimal getPrice() {
        return price;
    }


    public void setPrice(BigDecimal price) {
        this.price = price;
    }


    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }


    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }


    public Calendar getOpened() {
        return opened;
    }


    public void setOpened(Calendar opened) {
        this.opened = opened;
    }


    public Calendar getClosed() {
        return closed;
    }


    public void setClosed(Calendar closed) {
        this.closed = closed;
    }


    public Boolean getCancelInitiated() {
        return cancelInitiated;
    }


    public void setCancelInitiated(Boolean cancelInitiated) {
        this.cancelInitiated = cancelInitiated;
    }


    public Boolean getImmediateOrCancel() {
        return immediateOrCancel;
    }


    public void setImmediateOrCancel(Boolean immediateOrCancel) {
        this.immediateOrCancel = immediateOrCancel;
    }


    public Boolean getIsConditional() {
        return getConditional();
    }


    public void setIsConditional(Boolean isConditional) {
        this.setConditional(isConditional);
    }


    public String getCondition() {
        return condition;
    }


    public void setCondition(String condition) {
        this.condition = condition;
    }


    public String getConditionTarget() {
        return conditionTarget;
    }


    public void setConditionTarget(String conditionTarget) {
        this.conditionTarget = conditionTarget;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((orderUuid == null) ? 0 : orderUuid.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Order other = (Order) obj;
        if (orderUuid == null) {
            if (other.orderUuid != null)
                return false;
        } else if (!orderUuid.equals(other.orderUuid))
            return false;
        return true;
    }

    @Override
    public int compareTo(Order o) {
        int valor = 0;

        if (this.getOpened().compareTo(o.getOpened()) == 1)
            valor = 1;
        else if (this.getOpened().compareTo(o.getOpened()) == -1)
            valor = -1;

        return valor;
    }


    @Override
    public String toString() {
        return "Order [uuid=" + uuid + ", orderUuid=" + orderUuid + ", exchange=" + exchange + ", orderType="
                + orderType + ", quantity=" + quantity + ", quantityRemaining=" + quantityRemaining + ", limit=" + limit
                + ", comissionPaid=" + getComissionPaid() + ", price=" + price + ", pricePerUnit=" + pricePerUnit
                + ", opened=" + opened + ", closed=" + closed + ", cancelInitiated=" + cancelInitiated
                + ", immediateOrCancel=" + immediateOrCancel + ", isConditional=" + getConditional() + ", condition="
                + condition + ", conditionTarget=" + conditionTarget + "]";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return AccountId;
    }

    public void setAccountId(Long accountId) {
        AccountId = accountId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getReserved() {
        return reserved;
    }

    public void setReserved(BigDecimal reserved) {
        this.reserved = reserved;
    }

    public BigDecimal getReserveRemaining() {
        return reserveRemaining;
    }

    public void setReserveRemaining(BigDecimal reserveRemaining) {
        this.reserveRemaining = reserveRemaining;
    }

    public BigDecimal getComissionReserved() {
        return comissionReserved;
    }

    public void setComissionReserved(BigDecimal comissionReserved) {
        this.comissionReserved = comissionReserved;
    }

    public Boolean getOpen() {
        return isOpen;
    }

    public void setOpen(Boolean open) {
        isOpen = open;
    }

    public Boolean getConditional() {
        return isConditional;
    }

    public void setConditional(Boolean conditional) {
        isConditional = conditional;
    }

    public String getSentinel() {
        return sentinel;
    }

    public void setSentinel(String sentinel) {
        this.sentinel = sentinel;
    }

    public BigDecimal getComissionReservedRemaining() {
        return comissionReservedRemaining;
    }

    public void setComissionReservedRemaining(BigDecimal comissionReservedRemaining) {
        this.comissionReservedRemaining = comissionReservedRemaining;
    }

    public BigDecimal getComission() {
        return comission;
    }

    public void setComission(BigDecimal comission) {
        this.comission = comission;
    }

    public Calendar getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Calendar timeStamp) {
        this.timeStamp = timeStamp;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public String getConditionType() {
        return conditionType;
    }

    public void setConditionType(String conditionType) {
        this.conditionType = conditionType;
    }

    public String getTimeInEffect() {
        return timeInEffect;
    }

    public void setTimeInEffect(String timeInEffect) {
        this.timeInEffect = timeInEffect;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }
}
