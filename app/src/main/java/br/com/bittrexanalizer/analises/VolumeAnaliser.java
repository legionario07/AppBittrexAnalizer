package br.com.bittrexanalizer.analises;

import java.math.BigDecimal;
import java.util.LinkedList;

import br.com.bittrexanalizer.domain.Candle;
import br.com.bittrexanalizer.utils.ConstantesUtil;
import br.com.bittrexanalizer.utils.SessionUtil;

public class VolumeAnaliser implements IAnaliser<Candle> {

    private LinkedList<BigDecimal> listaOBV;
    private int qtdeDiasOBV = 0;
    private int qtdeFechamentosOBV = 0;
    private int retorno = SEM_OSCILACAO;


    public VolumeAnaliser() {

        listaOBV = new LinkedList<>();

        this.qtdeFechamentosOBV = new Integer(SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.OBV_QTDE_FECHAMENTOS));
        qtdeDiasOBV = qtdeFechamentosOBV *2;
    }

    @Override
    public int analizer(LinkedList<Candle> candlesTotal) {

        // IFR = 100 -(100 / (1 +FR)) ***FR= (MediaDeGanhos / MediaDePerdas)


        //verifica o tamanho dos dados se for menor que o QTDERDIASIFR não sera possivel realizar a Analizse
        if (getCandles(candlesTotal, qtdeDiasOBV) == null) {
            return -ERRO;
        }

        //Filtra a lista com apenas a quantidadeDesejada
        LinkedList<Candle> candles = getCandles(candlesTotal, qtdeDiasOBV);

        BigDecimal aux = BigDecimal.ZERO;

        //=SE(E3 = E2; H2; SE(E3 > E2; H2 + G3; H2 - G3 ))
        for (int i = 0; i < candles.size(); i++) {
            if (i == 0) {
                listaOBV.add(candles.get(i).getV());
            } else {
                //É igual?
                if (candles.get(i).getC().compareTo(candles.get(i - 1).getC()) == 0) {
                    listaOBV.add(listaOBV.get(i - 1).add(BigDecimal.ZERO));
                } else if (candles.get(i).getC().compareTo(candles.get(i - 1).getC()) == 1) {
                    listaOBV.add(listaOBV.get(i - 1).add(candles.get(i).getV()));
                } else {
                    listaOBV.add(listaOBV.get(i - 1).subtract(candles.get(i).getV()));
                }
            }
        }


        retorno = calcular(listaOBV, candles);

        return retorno;

    }

    private int calcular(LinkedList<BigDecimal> listaOBV, LinkedList<Candle> candles) {


        int retorno = SEM_OSCILACAO;
        boolean devoComprar = false;

        int indice = listaOBV.size() - qtdeFechamentosOBV;

        for (; indice < listaOBV.size(); indice++) {
            if (listaOBV.get(indice).compareTo(listaOBV.get(indice-1)) == 1) {
                devoComprar = true;
            } else {
                devoComprar = false;
                return retorno;
            }
        }

        if(devoComprar){
            retorno = IDEAL_PARA_COMPRA;
        }

        return retorno;
    }

    private static LinkedList<Candle> getCandles(LinkedList<Candle> candles, int qtde) {

        LinkedList<Candle> candlesRetorno = new LinkedList<>();

        if (candles.size() < qtde) {
            return null;
        }

        for (int i = candles.size() - qtde; i < candles.size(); i++) {
            candlesRetorno.add(candles.get(i));
        }

        return candlesRetorno;

    }


}
