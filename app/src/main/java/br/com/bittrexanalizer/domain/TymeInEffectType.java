package br.com.bittrexanalizer.domain;

/**
 * Created by PauLinHo on 02/02/2018.
 */

enum TymeInEffectType {

    GOOD_TIL_CANCELED("GOOD_TIL_CANCELED"),
    IMMEDIATE("IMMEDIATE OR CANCEL");


    private String condition;

    private TymeInEffectType(String condition) {
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }
}
