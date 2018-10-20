package br.com.bittrexanalizer.domain;

/**
 * Created by PauLinHo on 02/02/2018.
 */

enum ConditionType {

    NONE("NONE"),
    GREATER("GREATER THAN OR EQUAL TO"),
    LESS("LESS THAN OR EQUAL TO");


    private String condition;

    private ConditionType(String condition) {
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }
}
