package br.com.bittrexanalizer.utils;

import java.math.BigDecimal;

/**
 * Created by PauLinHo on 16/09/2017.
 */

public class DecimalFormatUtil {

    public static BigDecimal getBigDecimalFormatado(BigDecimal valor){

        BigDecimal numFormatado = valor.setScale(2, BigDecimal.ROUND_UP);

        return numFormatado;
    }

}
