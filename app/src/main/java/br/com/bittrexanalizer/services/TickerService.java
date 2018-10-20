package br.com.bittrexanalizer.services;

import java.util.LinkedList;

import br.com.bittrexanalizer.domain.EntidadeDomain;
import br.com.bittrexanalizer.domain.Ticker;
import br.com.bittrexanalizer.webserver.HttpClient;

/**
 * Created by PauLinHo on 21/01/2018.
 */

public class TickerService extends EntidadeDomain implements IService  {

    private LinkedList<Ticker> tickers;


    public TickerService(){
        tickers = new LinkedList<>();
    }

    public LinkedList<Ticker> getTickers() {
        return tickers;
    }

    public void setTickers(LinkedList<Ticker> tickers) {
        this.tickers = tickers;
    }

    private synchronized void atualizarTicker(Ticker ticker){

        Ticker t = new Ticker();
        t = (Ticker) ticker;
        ticker = HttpClient.find(ticker.getUrlApi());

        if(ticker!=null) {
            t.setLast(ticker.getLast());
            t.setAsk(ticker.getAsk());
            t.setBid(ticker.getBid());

        }

        tickers.add(t);

    }

    @Override
    public void run() {

        atualizarTicker((Ticker)this);

    }

}
