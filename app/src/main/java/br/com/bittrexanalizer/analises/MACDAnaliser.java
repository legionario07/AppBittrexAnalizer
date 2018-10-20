package br.com.bittrexanalizer.analises;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;

import br.com.bittrexanalizer.domain.Candle;
import br.com.bittrexanalizer.utils.ConstantesUtil;
import br.com.bittrexanalizer.utils.SessionUtil;

/**
 * Created by PauLinHo on 12/01/2018.
 */

public class MACDAnaliser implements IAnaliser<Candle> {

    private Integer qtdeDiasLongEMA = new Integer(26);
    private Integer qtdeDiasShortEMA = new Integer(12);
    private Integer qtdeDiasSIGNAL = new Integer(9);
    private String sigla;
    private int retorno = SEM_OSCILACAO;

    public MACDAnaliser() {
        qtdeDiasLongEMA = new Integer(SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.LONG_EMA));
        qtdeDiasShortEMA = new Integer(SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.SHORT_EMA));
        qtdeDiasSIGNAL = new Integer(SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.SIGNAL));
    }

    @Override
    public int analizer(LinkedList<Candle> candles) {

        sigla = candles.getFirst().getSigla();

        LinkedList<BigDecimal> listaShortEMA = new LinkedList<BigDecimal>();
        LinkedList<BigDecimal> listaLongEMA = new LinkedList<BigDecimal>();
        LinkedList<BigDecimal> listaMACD = new LinkedList<BigDecimal>();
        LinkedList<BigDecimal> listaSignal = new LinkedList<BigDecimal>();
        LinkedList<BigDecimal> listaHistogram = new LinkedList<BigDecimal>();

        //verifica o tamanho dos dados se for menor que o EMA LONG não sera possivel realizar a Analise
        if(getCandles(candles, qtdeDiasLongEMA)==null){
            return -ERRO;
        }

        listaShortEMA = getListaEMA(getCandles(candles, qtdeDiasShortEMA), qtdeDiasShortEMA);
        listaLongEMA = getListaEMA(getCandles(candles, qtdeDiasLongEMA), qtdeDiasLongEMA);
        listaMACD = getMACD(listaShortEMA, listaLongEMA);
        listaSignal = getListaSignal(listaMACD, qtdeDiasSIGNAL);
        listaHistogram = getHistogram(listaMACD, listaSignal);

        retorno = calcular(listaMACD, listaSignal, listaHistogram);

        return retorno;

    }

    private int calcular(LinkedList<BigDecimal> listaMACD, LinkedList<BigDecimal> listaSignal, LinkedList<BigDecimal> listaHistogram) {

        int retorno = SEM_OSCILACAO;

        if (listaMACD.getLast().compareTo(BigDecimal.ZERO.setScale(8, BigDecimal.ROUND_HALF_EVEN)) == -1) {
            return IDEAL_PARA_VENDA;
        }

        int tamanhoListaHistograma = listaHistogram.size();

//        for (int i = listaHistogram.size() - 2; i < listaHistogram.size(); i++) {
//            if (listaHistogram.get(tamanhoListaHistograma - 2).compareTo(BigDecimal.ZERO) == -1) {
//                if (listaHistogram.get(tamanhoListaHistograma - 1).compareTo(BigDecimal.ZERO) == 1) {
//                    return IDEAL_PARA_COMPRA;
//                }
//            }
//        }

        BigDecimal ultimoMACD = listaMACD.getLast();
        BigDecimal ultimoSignal = listaSignal.getLast();

        if(ultimoMACD.compareTo(BigDecimal.ZERO.setScale(8))==1){
            if(ultimoMACD.compareTo(ultimoSignal)==1){
                retorno = IDEAL_PARA_COMPRA;
            }
        }

        return retorno;

    }

    private LinkedList<BigDecimal> getListaEMA(LinkedList<Candle> dados, int qtdeDias) {

        LinkedList<BigDecimal> listaResultado = new LinkedList<BigDecimal>();

        BigDecimal mediaAnterior = new BigDecimal("0");
        mediaAnterior = getMedia(dados, qtdeDias);

        for (int j = qtdeDias; j < dados.size(); j++) {
            listaResultado.add(calcularEMA(dados.get(j), qtdeDias, mediaAnterior, j));
            mediaAnterior = listaResultado.getLast();
        }

        return listaResultado;

    }

    private LinkedList<BigDecimal> getListaSignal(LinkedList<BigDecimal> listaMACD, int qtdeDias) {

        LinkedList<BigDecimal> listaResultado = new LinkedList<BigDecimal>();

        BigDecimal mediaAnterior = new BigDecimal("0");
        BigDecimal porcentagem = new BigDecimal("0");
        BigDecimal resultado = new BigDecimal("0");

        for (int i = 0; i < qtdeDias; i++) {
            mediaAnterior = mediaAnterior.add(listaMACD.get(i));
        }

        mediaAnterior = mediaAnterior.divide(new BigDecimal(qtdeDias), 8, RoundingMode.HALF_EVEN);

        porcentagem = getPorcentagem(qtdeDias);

        for (int i = listaMACD.size() - qtdeDias; i < listaMACD.size(); i++) {
            resultado = new BigDecimal("0");
            resultado = listaMACD.get(i).subtract(mediaAnterior);
            resultado = resultado.multiply(porcentagem);
            resultado = resultado.add(mediaAnterior).setScale(8, RoundingMode.HALF_EVEN);

            listaResultado.add(resultado);
        }

        return listaResultado;

    }

    private BigDecimal getMedia(LinkedList<Candle> lista, int qtdeDias) {
        BigDecimal media = new BigDecimal("0");

        for (int i = 0; i < qtdeDias; i++) {
            media = media.add(lista.get(i).getC());
        }

        media = media.divide(new BigDecimal(qtdeDias), 8, RoundingMode.HALF_EVEN);

        return media;

    }

    private BigDecimal calcularEMA(Candle candle, int qtdeDias, BigDecimal mediaAnterior, int contador) {

        BigDecimal resultado = new BigDecimal("0.0");
        BigDecimal porcentagem = getPorcentagem(qtdeDias);

        resultado = new BigDecimal("0");
        resultado = candle.getC().subtract(mediaAnterior);
        resultado = resultado.multiply(porcentagem);
        resultado = resultado.add(mediaAnterior).setScale(8, RoundingMode.HALF_EVEN);

        return resultado;

    }

    /**
     * Calcula a porcentagem para realizar o calculo das EMA
     *
     * @param qtdeDias - A quantidade em Dias que terá a EMA
     * @return - Um BIGDecimal com a porcentagem
     */
    private BigDecimal getPorcentagem(Integer qtdeDias) {

        BigDecimal porcentagem = new BigDecimal("0");

        qtdeDias += 1;

        porcentagem = new BigDecimal(2).divide(new BigDecimal(qtdeDias), 8, RoundingMode.HALF_EVEN);

        return porcentagem;

    }

    private LinkedList<BigDecimal> getMACD(LinkedList<BigDecimal> listaShortEMA, LinkedList<BigDecimal> listaLongEMA) {

        LinkedList<BigDecimal> listaMACD = new LinkedList<>();
        BigDecimal temp = null;

        for (int i = 0; i < listaShortEMA.size(); i++) {

            temp = new BigDecimal("0");

            temp = listaShortEMA.get(i).subtract(listaLongEMA.get(i));

            listaMACD.add(temp);

        }

        return listaMACD;

    }

    private LinkedList<BigDecimal> getHistogram(LinkedList<BigDecimal> listaMACD, LinkedList<BigDecimal> listaSignal) {

        LinkedList<BigDecimal> listaHistogram = new LinkedList<>();
        BigDecimal temp = null;

        for (int i = 0; i < listaSignal.size(); i++) {

            temp = new BigDecimal("0");

            temp = listaMACD.get(i).subtract(listaSignal.get(i));

            listaHistogram.add(temp);

        }

        return listaHistogram;

    }

    private static LinkedList<Candle> getCandles(LinkedList<Candle> candles, int qtde) {

        LinkedList<Candle> candlesRetorno = new LinkedList<>();

        if (candles.size() < (qtde*3)) {
            return null;
        }

        for (int i = candles.size() - (qtde*3); i < candles.size()-1; i++) {
            candlesRetorno.add(candles.get(i));
        }

        return candlesRetorno;

    }


}
