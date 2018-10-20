package br.com.bittrexanalizer.analises;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;

import br.com.bittrexanalizer.domain.Candle;
import br.com.bittrexanalizer.utils.ConstantesUtil;
import br.com.bittrexanalizer.utils.SessionUtil;

public class IFRAnaliser implements IAnaliser<Candle> {

    private BigDecimal IFR;
    private BigDecimal mediaDeGanhos;
    private BigDecimal mediaDePerdas;
    private int qtdeDiasIFR = 0;
    private int retorno = SEM_OSCILACAO;


    public IFRAnaliser(){

        this.qtdeDiasIFR = new Integer(SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.IFR_DIAS));
    }

    @Override
    public int analizer(LinkedList<Candle> candlesTotal) {

        // IFR = 100 -(100 / (1 +FR)) ***FR= (MediaDeGanhos / MediaDePerdas)


        //verifica o tamanho dos dados se for menor que o QTDERDIASIFR não sera possivel realizar a Analizse
        if(getCandles(candlesTotal, qtdeDiasIFR)==null){
            return -ERRO;
        }

        //Filtra a lista com apenas a quantidadeDesejada
        LinkedList<Candle> candles = getCandles(candlesTotal, qtdeDiasIFR);

        int qtdeDiasNegativo = 0;
        int qtdeDiasPositivo = 0;
        BigDecimal valorNegativoSomado = new BigDecimal("0.0");
        BigDecimal valorPositivoSomado = new BigDecimal("0.0");

        // pega os valores
        for (int i = 0; i < candles.size() - 1; i++) {

            // negativo
            if (candles.get(i).getC().compareTo(candles.get(i + 1).getC()) == 1) {
                qtdeDiasNegativo++;
                valorNegativoSomado = valorNegativoSomado
                        .add(candles.get(i).getC().subtract(candles.get(i + 1).getC()));
            } else if (candles.get(i).getC().compareTo(candles.get(i + 1).getC()) == -1) {
                qtdeDiasPositivo++;
                valorPositivoSomado = valorPositivoSomado
                        .add(candles.get(i + 1).getC().subtract(candles.get(i).getC()));
            }

        }

        // efetua o calculo
        mediaDePerdas = BigDecimal.ZERO;
        mediaDePerdas = valorNegativoSomado.divide(new BigDecimal(qtdeDiasNegativo), 8, RoundingMode.HALF_EVEN);

        mediaDeGanhos = BigDecimal.ZERO;
        mediaDeGanhos = valorPositivoSomado.divide(new BigDecimal(qtdeDiasPositivo), 8, RoundingMode.HALF_EVEN);

        // IFR = 100 -(100 / (1 +FR)) ***FR= (MediaDeGanhos / MediaDePerdas)

        IFR = BigDecimal.ZERO;

        BigDecimal fr = BigDecimal.ZERO;

        fr = mediaDeGanhos.divide(mediaDePerdas, 8, RoundingMode.HALF_EVEN);

        BigDecimal aux = BigDecimal.ONE.add(fr);
        BigDecimal aux2 = new BigDecimal("100").divide(aux, 8, RoundingMode.HALF_EVEN);

        IFR = new BigDecimal("100").subtract(aux2);
        IFR = IFR.subtract(new BigDecimal(5.15));

        retorno = calcular(IFR);

        return retorno;

    }

    private int calcular(BigDecimal IFR) {


        int retorno = SEM_OSCILACAO;

        if(IFR.compareTo(new BigDecimal(SessionUtil.getInstance().getMapConfiguracao().get("IFR_MIN")))==-1){
            return IDEAL_PARA_COMPRA;
        }

        if(IFR.compareTo(new BigDecimal(SessionUtil.getInstance().getMapConfiguracao().get("IFR_MAX")))==1){
            return IDEAL_PARA_VENDA;
        }

        return retorno;
    }

    private static LinkedList<Candle> getCandles(LinkedList<Candle> candles, int qtde) {

        LinkedList<Candle> candlesRetorno = new LinkedList<>();

        if(candles.size()<qtde*3){
            return null;
        }

        for (int i = candles.size()-qtde*3; i < candles.size()-1; i++) {
            candlesRetorno.add(candles.get(i));
        }

        return candlesRetorno;

    }


}
