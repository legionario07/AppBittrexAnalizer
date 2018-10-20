package br.com.bittrexanalizer.domain;

import java.util.LinkedList;

import br.com.bittrexanalizer.webserver.HttpClient;

/**
 * Created by PauLinHo on 09/01/2018.
 */

public class AbstractTicker extends EntidadeDomain implements Runnable {

    private static LinkedList<Ticker> tickers;

    public AbstractTicker(){
        tickers = new LinkedList<>();
    }

    public static LinkedList<Ticker> getTickers() {
        return tickers;
    }

    public static void setTickers(LinkedList<Ticker> tickers) {
        AbstractTicker.tickers = tickers;
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

      //  atualizarTicker((Ticker) this);

    }
}
