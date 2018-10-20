package br.com.bittrexanalizer.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import br.com.bittrexanalizer.domain.Candle;
import br.com.bittrexanalizer.domain.EntidadeDomain;
import br.com.bittrexanalizer.facade.CompraFacade;
import br.com.bittrexanalizer.strategy.AlarmAnalizerCompraStrategy;
import br.com.bittrexanalizer.strategy.CandleStrategy;
import br.com.bittrexanalizer.utils.ConstantesUtil;
import br.com.bittrexanalizer.utils.SessionUtil;
import br.com.bittrexanalizer.utils.WebServiceUtil;
import br.com.bittrexanalizer.webserver.HttpClient;

/**
 * Created by PauLinHo on 21/01/2018.
 */

public class CandleService extends EntidadeDomain implements IService {

    private LinkedList<Candle> candles;
    private volatile static Map<String, LinkedList<Candle>> mapCandles;


    public CandleService() {
        mapCandles = Collections.synchronizedMap(new HashMap<String, LinkedList<Candle>>());
    }


    private synchronized void buscarCandles(Candle candle) {

        Candle c = new Candle();
        c = candle;

        LinkedList<Candle> candles = new LinkedList<>();

        String url = WebServiceUtil.construirURLTicker(WebServiceUtil.getUrlTicks(),
                c.getSigla(), SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.PERIODICIDADE));
        String dados = HttpClient.find(url, null);
        if (dados != null) {
            if (WebServiceUtil.verificarRetorno(dados)) {
                candles = new CandleStrategy().getObjects(dados);
                if (AlarmAnalizerCompraStrategy.mapCandles != null) {
                    AlarmAnalizerCompraStrategy.mapCandles.put(c.getSigla(), candles);
                }

                if (CompraFacade.mapCandles != null) {
                    CompraFacade.mapCandles.put(c.getSigla(), candles);
                }

            }
        }
    }

    @Override
    public void run() {

        buscarCandles((Candle) this);

    }

    public LinkedList<Candle> getCandles() {
        return candles;
    }

    public void setCandles(LinkedList<Candle> candles) {
        this.candles = candles;
    }

    public static Map<String, LinkedList<Candle>> getMapCandles() {
        return CandleService.mapCandles;
    }

    public static void setMapCandles(Map<String, LinkedList<Candle>> mapCandles) {
        CandleService.mapCandles = mapCandles;
    }

}
