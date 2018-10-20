package br.com.bittrexanalizer.domain;

/**
 * Created by PauLinHo on 02/02/2018.
 */

enum OrderType {

    LIMIT("LIMIT"),
    CONDITIONAL("CONDITIONAL");


    private String condition;

    private OrderType(String condition) {
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }
}
