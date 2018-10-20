package br.com.bittrexanalizer.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import br.com.bittrexanalizer.domain.Ticker;


/**
 * Created by PauLinHo on 30/09/2017.
 */

public class TickerComparator  {

    public static LinkedList<Ticker> ordenar(String criterio, LinkedList<Ticker> listaDesordenada){

        switch (criterio) {

            case "NOME ASC":
                Collections.sort(listaDesordenada, getNomeAsc());
                break;

            case "NOME DESC":
                Collections.sort(listaDesordenada, getNomeDesc());
                break;

            case "LAST ASC":
                Collections.sort(listaDesordenada, getLastAsc());
                break;

            case "LAST DESC":
                Collections.sort(listaDesordenada, getLastDesc());
                break;

        }

        return listaDesordenada;

    }



    private static Comparator<Ticker> getNomeAsc() {
        return new Comparator<Ticker>() {

            @Override
            public int compare(Ticker o1, Ticker o2) {
                int valor = o2.getNomeExchange().compareTo(o1.getNomeExchange()) * -1;
                // se for igual, comparar por sigla
                if (valor == 0) {
                    return o2.getSigla().compareTo(o1.getSigla());
                }
                return valor;
            }
        };
    }

    private static Comparator<Ticker> getNomeDesc() {
        return new Comparator<Ticker>() {

            @Override
            public int compare(Ticker o1, Ticker o2) {
                int valor = o1.getNomeExchange().compareTo(o2.getNomeExchange()) * -1;
                // se for igual, comparar por sigla
                if (valor == 0) {
                    return o1.getSigla().compareTo(o2.getSigla());
                }
                return valor;
            }
        };
    }

    private static Comparator<Ticker> getLastAsc() {
        return new Comparator<Ticker>() {
            @Override
            public int compare(Ticker o1, Ticker o2) {
                int valor = o2.getLast().compareTo(o1.getLast()) * -1;
                // se for igual, comparar por Nome
                if (valor == 0) {
                    return o2.getNomeExchange().compareTo(o1.getNomeExchange());
                }
                return valor;
            }
        };
    }

    private static Comparator<Ticker> getLastDesc() {
        return new Comparator<Ticker>() {
            @Override
            public int compare(Ticker o1, Ticker o2) {
                int valor = o1.getLast().compareTo(o2.getLast()) * -1;
                // se for igual, comparar por Nome
                if (valor == 0) {
                    return o1.getNomeExchange().compareTo(o2.getNomeExchange());
                }
                return valor;
            }
        };
    }

}
