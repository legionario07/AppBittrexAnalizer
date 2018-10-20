package br.com.bittrexanalizer.strategy;

import java.text.SimpleDateFormat;
import java.util.LinkedList;

/**
 * Created by PauLinHo on 17/01/2018.
 */

public interface IStrategy<T extends Object> {

    static final String BITCOIN_BASE64 = "1NwaKCqfb532vVRFmBAixPbGQJhTfwpbzk";
    static final String BITCOIN = "BITCOIN";
    SimpleDateFormat SDF_DDMMYYYY_HHMMSS = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    SimpleDateFormat SDF_DDMMYYYY = new SimpleDateFormat("yyyy/MM/dd");


    LinkedList<T> getObjects(String dados);
}
