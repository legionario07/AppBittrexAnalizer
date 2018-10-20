package br.com.bittrexanalizer.domain;

/**
 * Created by PauLinHo on 04/02/2018.
 */

public class OrderHistory extends EntidadeDomain {

    // POST https://bittrex.com/api/v2.0/auth/market/TradeBuy with data { MarketName: "BTC-DGB,
    // OrderType:"LIMIT", Quantity: 10000.02, Rate: 0.0000004, TimeInEffect:"GOOD_TIL_CANCELED",
    // ConditionType: "NONE", Target: 0, __RequestVerificationToken: "HIDDEN_FOR_PRIVACY"}

    /*MarketName:string, OrderType:string, Quantity:float,
    Rate:float, TimeInEffect:string,ConditionType:string,
    Target:int __RequestVerificationToken:string*/

    private String MarketName;
    private String OrderType;
    private float Quantity;
    private float Rate;
    private String TimeInEffect;
    private String ConditionType;
    private int Target;
    private String _RequestVerificationToken;

    public String getMarketName() {
        return MarketName;
    }

    public void setMarketName(String marketName) {
        MarketName = marketName;
    }

    public String getOrderType() {
        return OrderType;
    }

    public void setOrderType(String orderType) {
        OrderType = orderType;
    }

    public float getQuantity() {
        return Quantity;
    }

    public void setQuantity(float quantity) {
        Quantity = quantity;
    }

    public float getRate() {
        return Rate;
    }

    public void setRate(float rate) {
        Rate = rate;
    }

    public String getTimeInEffect() {
        return TimeInEffect;
    }

    public void setTimeInEffect(String timeInEffect) {
        TimeInEffect = timeInEffect;
    }

    public String getConditionType() {
        return ConditionType;
    }

    public void setConditionType(String conditionType) {
        ConditionType = conditionType;
    }

    public int getTarget() {
        return Target;
    }

    public void setTarget(int target) {
        Target = target;
    }

    public String get_RequestVerificationToken() {
        return _RequestVerificationToken;
    }

    public void set_RequestVerificationToken(String _RequestVerificationToken) {
        this._RequestVerificationToken = _RequestVerificationToken;
    }
}
