package br.com.bittrexanalizer.analises;

import java.math.BigDecimal;
import java.util.LinkedList;

import br.com.bittrexanalizer.domain.Candle;
import br.com.bittrexanalizer.utils.ConstantesUtil;
import br.com.bittrexanalizer.utils.SessionUtil;

/**
 * Created by PauLinHo on 06/02/2018.
 */

public class AnaliseRobot implements IAnaliser<Candle> {

    private LinkedList<BigDecimal> lasts;
    private LinkedList<BigDecimal> volumes;
    private BigDecimal mediaVolume;
    private int numeroDeVerificacoes;

    public AnaliseRobot(){
        lasts = new LinkedList<>();
        volumes = new LinkedList<>();
        mediaVolume = BigDecimal.ZERO.setScale(8);
        numeroDeVerificacoes = Integer.valueOf(SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.ROBOT_VEZES));
    }


    @Override
    public int analizer(LinkedList<Candle> candles) {


        //verifica o tamanho dos dados se for menor que o QTDERDIASIFR não sera possivel realizar a Analizse
        if(getCandles(candles, numeroDeVerificacoes)==null){
            return -ERRO;
        }

        lasts = getCandles(candles, numeroDeVerificacoes);
        volumes = getVolumes(candles, numeroDeVerificacoes*2);

        for(BigDecimal b : volumes){
            mediaVolume.add(b);
        }

        mediaVolume = mediaVolume.divide(new BigDecimal(volumes.size()), BigDecimal.ROUND_HALF_EVEN);

        //Se o volume não for maior que a média de volume
        if(mediaVolume.compareTo(volumes.get(volumes.size()-2))==-1){
            return IFRAnaliser.SEM_OSCILACAO;
        }

        for(int i = 0; i < lasts.size()-1; i++){
            if(!devoComprar(lasts.get(i), lasts.get(i+1))){
                return IFRAnaliser.SEM_OSCILACAO;
            }
        }




        return IFRAnaliser.IDEAL_PARA_COMPRA;
    }

    private boolean devoComprar(BigDecimal ultimo, BigDecimal atual){

        //o atual é maior
        if(atual.compareTo(ultimo)!=-1){
            return true;
        }else{
            return false;
        }

    }

    private static LinkedList<BigDecimal> getCandles(LinkedList<Candle> candles, int qtde) {

        LinkedList<BigDecimal> candlesRetorno = new LinkedList<>();

        if (candles.size() < qtde) {
            return null;
        }

        for (int i = candles.size() - qtde; i < candles.size(); i++) {
            candlesRetorno.add(candles.get(i).getL());
        }

        return candlesRetorno;

    }

    private static LinkedList<BigDecimal> getVolumes(LinkedList<Candle> candles, int qtde) {

        LinkedList<BigDecimal> candlesRetorno = new LinkedList<>();

        if (candles.size() < qtde) {
            return null;
        }

        for (int i = candles.size() - qtde; i < candles.size(); i++) {
            candlesRetorno.add(candles.get(i).getV());
        }

        return candlesRetorno;

    }
}
