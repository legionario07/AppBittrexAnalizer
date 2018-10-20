package br.com.bittrexanalizer.domain;

import java.math.BigDecimal;
import java.util.Calendar;

import br.com.bittrexanalizer.services.CandleService;

public class Candle extends CandleService implements Comparable<Candle> {

	/*BV: 13.14752793,          // base volume
    C: 0.000121,              // close
	H: 0.00182154,            // high
	L: 0.0001009,             // low
	O: 0.00182154,            // open
	T: "2017-07-16T23:00:00", // timestamp
	V: 68949.3719684          // 24h volume*/

    /**
     * BASE VOLUME
     */
    private BigDecimal BV = new BigDecimal("0");
    /**
     * CLOSE
     */
    private BigDecimal C;
    /**
     * HIGH
     */
    private BigDecimal H;
    /**
     * LOW
     */
    private BigDecimal L;
    /**
     * OPEN
     */
    private BigDecimal O;
    /**
     * TIMESTAMP
     */
    private Calendar T = Calendar.getInstance();
    /**
     * VOLUME
     */
    private BigDecimal V;

    private String sigla;

    public Candle(){

    }

    public Candle(Double C, Double L, Double H){
        this.setC(new BigDecimal(C));
        this.setL(new BigDecimal(L));
        this.setH(new BigDecimal(H));
    }

    public BigDecimal getBV() {
        return BV;
    }

    public void setBV(BigDecimal bV) {
        BV = bV;
    }

    public BigDecimal getC() {
        return C;
    }

    public void setC(BigDecimal c) {
        C = c;
    }

    public BigDecimal getH() {
        return H;
    }

    public void setH(BigDecimal h) {
        H = h;
    }

    public BigDecimal getL() {
        return L;
    }

    public void setL(BigDecimal l) {
        L = l;
    }

    public BigDecimal getO() {
        return O;
    }

    public void setO(BigDecimal o) {
        O = o;
    }

    public Calendar getT() {
        return T;
    }

    public void setT(Calendar t) {
        T = t;
    }

    public BigDecimal getV() {
        return V;
    }

    public void setV(BigDecimal v) {
        V = v;
    }

    @Override
    public String toString() {
        return "Candle [BV=" + BV + ", C=" + C + ", H=" + H + ", L=" + L + ", O=" + O + ", T=" + T + ", V=" + V + "]";
    }

    @Override
    public int compareTo(Candle o) {
        int value = 0;

        if (this.T.compareTo(o.T) == 1)
            value = 1;
        else if (this.T.compareTo(o.T) == -1)
            value = -1;

        return value;
    }


    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }
}
