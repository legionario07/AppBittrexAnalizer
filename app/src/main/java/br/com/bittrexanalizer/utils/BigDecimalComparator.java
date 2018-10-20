package br.com.bittrexanalizer.utils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;


/**
 * Created by PauLinHo on 30/09/2017.
 */

public class BigDecimalComparator {

    public static LinkedList<BigDecimal> ordenar(String criterio, LinkedList<BigDecimal> listaDesordenada){

        switch (criterio) {

            case "CLOSE ASC":
                Collections.sort(listaDesordenada, getCloseAsc());
                break;

            case "LOW ASC":
                Collections.sort(listaDesordenada, getLowAsc());
                break;

            case "HIGH ASC":
                Collections.sort(listaDesordenada, getHighAsc());
                break;


        }

        return listaDesordenada;

    }



    private static Comparator<BigDecimal> getCloseAsc() {
        return new Comparator<BigDecimal>() {

            @Override
            public int compare(BigDecimal o1, BigDecimal o2) {
                int valor = o2.compareTo(o1) * -1;
                return valor;
            }
        };
    }

    private static Comparator<BigDecimal> getHighAsc() {
        return new Comparator<BigDecimal>() {

            @Override
            public int compare(BigDecimal o1, BigDecimal o2) {
                int valor = o2.compareTo(o1) * -1;
                return valor;
            }
        };
    }

    private static Comparator<BigDecimal> getLowAsc() {
        return new Comparator<BigDecimal>() {

            @Override
            public int compare(BigDecimal o1, BigDecimal o2) {
                int valor = o2.compareTo(o1) * -1;
                return valor;
            }
        };
    }



}
