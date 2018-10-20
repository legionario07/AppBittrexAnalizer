package br.com.bittrexanalizer.analises;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;

import br.com.bittrexanalizer.domain.Candle;
import br.com.bittrexanalizer.utils.BigDecimalComparator;
import br.com.bittrexanalizer.utils.ConstantesUtil;
import br.com.bittrexanalizer.utils.SessionUtil;

/**
 * Created by PauLinHo on 12/01/2018.
 */

public class OsciladorEstocasticoAnaliser implements IAnaliser<Candle> {

    private Integer periodoK = new Integer(14);
    private Integer periodoD = new Integer(3);
    private int retorno = SEM_OSCILACAO;
    private static final int SET_SCALE =8;

    private static final String CRITERIO_ORDENACAO_LOW_ASC = "LOW ASC";
    private static final String CRITERIO_ORDENACAO_HIGH_ASC = "HIGH ASC";
    private static final String CRITERIO_ORDENACAO_CLOSE_ASC = "CLOSE ASC";

    public OsciladorEstocasticoAnaliser() {
         periodoK = new Integer(SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.OE_TEMPO_PERIODO_K));
        periodoD = new Integer(SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.OE_TEMPO_PERIODO_D));
    }

    @Override
    public int analizer(LinkedList<Candle> candles) {


        LinkedList<BigDecimal> fechamentos = new LinkedList<BigDecimal>();
        LinkedList<BigDecimal> minimas = new LinkedList<BigDecimal>();
        LinkedList<BigDecimal> maximas = new LinkedList<BigDecimal>();
        LinkedList<BigDecimal> listaPeriodoK = new LinkedList<BigDecimal>();
        LinkedList<BigDecimal> listaPeriodoD = new LinkedList<BigDecimal>();

        //verifica o tamanho dos dados se for menor que o PERIODO k não sera possivel realizar a Analise
        if (getCandles(candles, periodoK) == null) {
            return -ERRO;
        }

        fechamentos = getPeriodosFechamentos(getCandles(candles, periodoK));
        maximas = getMaximas(getCandles(candles, periodoK));
        minimas = getMinimas(getCandles(candles, periodoK));

        listaPeriodoK = getPeriodoK(fechamentos, maximas, minimas);
        listaPeriodoD = getPeriodoD(listaPeriodoK);


        retorno = calcular(listaPeriodoK, listaPeriodoD);

        return retorno;

    }

    private int calcular(LinkedList<BigDecimal> listaPeriodoK, LinkedList<BigDecimal> listaPeriodoD) {

        int retorno = SEM_OSCILACAO;

        int tamanhoTotal = listaPeriodoK.size();

        if (listaPeriodoK.get(tamanhoTotal - 1).compareTo(
                new BigDecimal(SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.OE_TAXA_MIN))) == -1 &&
                listaPeriodoK.get(tamanhoTotal).compareTo(
                        new BigDecimal(SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.OE_TAXA_MIN))) == 1) {
            return IDEAL_PARA_COMPRA;

        }

        return retorno;

    }

    private LinkedList<BigDecimal> getPeriodosFechamentos(LinkedList<Candle> dados) {

        LinkedList<BigDecimal> listaResultado = new LinkedList<BigDecimal>();

        for (int j = 0; j < dados.size(); j++) {
            listaResultado.add(dados.get(j).getC().setScale(SET_SCALE, BigDecimal.ROUND_HALF_EVEN));
        }

        return listaResultado;

    }

    private LinkedList<BigDecimal> getMaximas(LinkedList<Candle> dados) {

        LinkedList<BigDecimal> listaResultado = new LinkedList<BigDecimal>();

        for (int j = 0; j < dados.size(); j++) {
            listaResultado.add(dados.get(j).getH().setScale(SET_SCALE, BigDecimal.ROUND_HALF_EVEN));
        }

        return listaResultado;

    }

    private LinkedList<BigDecimal> getMinimas(LinkedList<Candle> dados) {

        LinkedList<BigDecimal> listaResultado = new LinkedList<BigDecimal>();

        for (int j = 0; j < dados.size(); j++) {
            listaResultado.add(dados.get(j).getL().setScale(SET_SCALE, BigDecimal.ROUND_HALF_EVEN));
        }

        return listaResultado;

    }


    private LinkedList<BigDecimal> getPeriodoK(LinkedList<BigDecimal> fechamentos, LinkedList<BigDecimal> maximas, LinkedList<BigDecimal> minimas) {

        //se(fechamento.get(11)<>0;(fechamento.get(11)-minimo(d7:d11))/maximo(e7:e11)-minimo(d7:d11));0)

        LinkedList<BigDecimal> listaPeriodoK = new LinkedList<>();


        for (int i = 0; i < fechamentos.size(); i++) {

            if (i + periodoK > fechamentos.size()) {
                i = fechamentos.size();
                continue;
            }

            BigDecimal fechamentoTemp = new BigDecimal("0").setScale(SET_SCALE, BigDecimal.ROUND_HALF_EVEN);
            BigDecimal aux = new BigDecimal("0").setScale(SET_SCALE, BigDecimal.ROUND_HALF_EVEN);
            BigDecimal minimaTemp = new BigDecimal("0").setScale(SET_SCALE, BigDecimal.ROUND_HALF_EVEN);
            BigDecimal maximaTemp = new BigDecimal("0").setScale(SET_SCALE, BigDecimal.ROUND_HALF_EVEN);
            LinkedList<BigDecimal> maximasTemp = copyOfRange(maximas, i, periodoK);
            LinkedList<BigDecimal> minimasTemp = copyOfRange(minimas, i, periodoK);

            fechamentoTemp = fechamentos.get(i + (periodoK - 1)).setScale(SET_SCALE, BigDecimal.ROUND_HALF_EVEN);

            maximasTemp = BigDecimalComparator.ordenar(CRITERIO_ORDENACAO_HIGH_ASC, maximasTemp);
            minimasTemp = BigDecimalComparator.ordenar(CRITERIO_ORDENACAO_LOW_ASC, minimasTemp);

            minimaTemp = minimasTemp.getFirst().setScale(SET_SCALE, RoundingMode.HALF_EVEN);
            fechamentoTemp = fechamentoTemp.subtract(minimaTemp).setScale(SET_SCALE, RoundingMode.HALF_EVEN);

            maximaTemp = maximasTemp.getLast().setScale(SET_SCALE, RoundingMode.HALF_EVEN);

            aux = maximaTemp.subtract(minimaTemp).setScale(SET_SCALE, RoundingMode.HALF_EVEN);

            BigDecimal retorno = fechamentoTemp.divide(aux, BigDecimal.ROUND_HALF_EVEN).setScale(SET_SCALE, RoundingMode.HALF_EVEN);

            listaPeriodoK.add(retorno.multiply(new BigDecimal("100")));

        }

        return listaPeriodoK;

    }

    private LinkedList<BigDecimal> getPeriodoD(LinkedList<BigDecimal> listaPeriodoK) {

        //=se(fechamento<>0;media(k11:k13)

        LinkedList<BigDecimal> listaPeriodoD = new LinkedList<>();


        for (int i = 0; i < listaPeriodoK.size(); i++) {

            if (i + periodoD > listaPeriodoK.size()) {
                i = listaPeriodoK.size();
                continue;
            }

            BigDecimal aux = new BigDecimal("0").setScale(SET_SCALE, BigDecimal.ROUND_HALF_EVEN);
            LinkedList<BigDecimal> intervaloDeMedia = copyOfRange(listaPeriodoK, i, periodoD);

            for (BigDecimal b : intervaloDeMedia) {
                aux = aux.add(b);
            }

            aux = aux.divide(new BigDecimal(periodoD), BigDecimal.ROUND_HALF_EVEN).setScale(SET_SCALE, RoundingMode.HALF_EVEN);


            listaPeriodoD.add(aux);

        }

        return listaPeriodoD;

    }

    private LinkedList<BigDecimal> copyOfRange(LinkedList<BigDecimal> original, int inicio, int tamanho) {

        LinkedList<BigDecimal> retorno = new LinkedList<>();


        for (int i = inicio; i < (inicio + tamanho); i++) {
            retorno.add(original.get(i));
        }

        return retorno;

    }


    private static LinkedList<Candle> getCandles(LinkedList<Candle> candles, int qtde) {

        LinkedList<Candle> candlesRetorno = new LinkedList<>();

        if (candles.size() < (qtde * 2)) {
            return null;
        }

        for (int i = candles.size() - ((qtde * 2) + 1); i < candles.size()-1; i++) {
            candlesRetorno.add(candles.get(i));
        }

        return candlesRetorno;

    }


}
