package br.com.bittrexanalizer.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import br.com.bittrexanalizer.domain.Configuracao;


/**
 * Created by PauLinHo on 30/09/2017.
 */

public class ConfiguracaoComparator {

    public static final String PROP_ASC = "PROPRIEDADE ASC";
    public static final String PROP_DESC = "PROPRIEDADE DESC";
    public static final String VALOR_ASC = "VALOR ASC";

    public static LinkedList<Configuracao> ordenar(String criterio, LinkedList<Configuracao> listaDesordenada){

        switch (criterio) {

            case "PROPRIEDADE ASC":
                Collections.sort(listaDesordenada, getPropriedadeASC());
                break;

            case "PROPRIEDADE DESC":
                Collections.sort(listaDesordenada, getPropriedadeDESC());
                break;

            case "VALOR ASC":
                Collections.sort(listaDesordenada, getValorASC());
                break;


        }

        return listaDesordenada;

    }



    private static Comparator<Configuracao> getPropriedadeASC() {
        return new Comparator<Configuracao>() {

            @Override
            public int compare(Configuracao o1, Configuracao o2) {
                int valor = o2.getPropriedade().compareTo(o1.getPropriedade()) * -1;
                return valor;
            }
        };
    }

    private static Comparator<Configuracao> getPropriedadeDESC() {
        return new Comparator<Configuracao>() {

            @Override
            public int compare(Configuracao o1, Configuracao o2) {
                int valor = o1.getPropriedade().compareTo(o2.getPropriedade()) * -1;
                return valor;
            }
        };
    }

    private static Comparator<Configuracao> getValorASC() {
        return new Comparator<Configuracao>() {

            @Override
            public int compare(Configuracao o1, Configuracao o2) {
                int valor = o2.getValor().compareTo(o1.getValor()) * -1;
                return valor;
            }
        };
    }



}
