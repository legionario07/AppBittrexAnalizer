package br.com.bittrexanalizer.strategy;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.LinkedList;

import br.com.bittrexanalizer.domain.Candle;

/**
 * Created by PauLinHo on 17/01/2018.
 */

public class CandleStrategy implements IStrategy<Candle> {

    private LinkedList<Candle> objetos;

    @Override
    public LinkedList<Candle> getObjects(String dados) {

        objetos = new LinkedList<>();

        dados = dados.replace("}", "");

        String[] dadosTemp = dados.split("\\{");

        int i = 0;
        //Se tiver muitos candles ele filtra só os ultimos 100
        if(dadosTemp.length>250) {
            i = dadosTemp.length - 110;
        }

        for (; i < dadosTemp.length; i++) {
            objetos.add(getCandle(dadosTemp[i]));
        }

        return objetos;
    }


    private synchronized static Candle getCandle(String dados) {

        Candle c = new Candle();

        String[] dadosTemp = dados.split(",");

        for (String s : dadosTemp) {

            String key[] = s.split(":");

            switch (key[0].replace("\"", "")) {
                case "O":
                    c.setO(new BigDecimal(key[1]));
                    break;
                case "H":
                    c.setH(new BigDecimal(key[1]));
                    break;
                case "L":
                    c.setL(new BigDecimal(key[1]));
                    break;
                case "C":
                    c.setC(new BigDecimal(key[1]));
                    break;
                case "V":
                    c.setV(new BigDecimal(key[1]));
                    break;
                case "T":
                    String dataTemp = key[1].replace("T", " ").replace("-", "/").replace("\"", "");
                    dataTemp = dataTemp.substring(0, 11);

                    try {
                        c.getT().setTime(SDF_DDMMYYYY.parse(dataTemp));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    break;
                case "BV":
                    c.setBV(new BigDecimal(key[1].replace("]", "")));
                    break;
            }

        }

        return c;
    }
}
