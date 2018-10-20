package br.com.bittrexanalizer.helpers;


import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.LinkedList;

import br.com.bittrexanalizer.domain.Ticker;
import br.com.bittrexanalizer.utils.SessionUtil;

/**
 * Created by PauLinHo on 08/10/2017.
 */

public class TelaHelper {

    private static Ticker ticker;
    /**
     * Get data for WebService
     */
    public static boolean getTicker() {

        ticker = new Ticker();

        try {
            LinkedList<Ticker> tickers = new LinkedList<>();

            SessionUtil.getInstance().setTickers(tickers);


        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * Simula um venda de BiTCoin
     *
     * @param valorEmBitCoin
     * @return - Um BigDecimal com o valor da venda
     */
    public static BigDecimal calcularVenda(Double valorEmBitCoin, BigDecimal taxaSell, Double TAXA_SELL) {

        BigDecimal sellTemp = taxaSell;
        Double doublePrecoSell = new Double(sellTemp.toString());

        BigDecimal totalSellReais;
        Double doubleTotalSellReais;

        doubleTotalSellReais = valorEmBitCoin * doublePrecoSell;
        doubleTotalSellReais -= doubleTotalSellReais * TAXA_SELL;

        DecimalFormat df = new DecimalFormat("#.##");
        String retorno = df.format(new Double(doubleTotalSellReais.toString()));

        totalSellReais = new BigDecimal(retorno.replace(",", "."));

        return totalSellReais;

    }

    public static String calcularCompra(BigDecimal valorEmReais, BigDecimal taxaBuy, Double TAXA_BUY) {

        if (!(valorEmReais.compareTo(new BigDecimal(0.0)) == 1)) {
            return "0.00000";
        }

        BigDecimal buyTemp = taxaBuy;
        Double doublePrecoBuy = new Double(buyTemp.toString());

        DecimalFormat df = new DecimalFormat("#.#####");
        Double doubleValorEmReais = new Double(valorEmReais.toString());

        Double totalBuyBitcoin;

        totalBuyBitcoin = doubleValorEmReais / doublePrecoBuy;
        totalBuyBitcoin -= totalBuyBitcoin * TAXA_BUY;

        String retorno = df.format(totalBuyBitcoin).replace(",", ".");

        return retorno;

    }


    /**
     *
     * @param valorAplicado
     * @return
     */
    public static BigDecimal calcularTaxaEDepositoNegocieCoins(BigDecimal valorAplicado) {

        DecimalFormat df = new DecimalFormat("#.##");

        Double TAXA_DEPOSITO = 0.009d;

        Double doubleValorAplicado = new Double(valorAplicado.toString());
        Double resultado = (doubleValorAplicado * TAXA_DEPOSITO);
        BigDecimal valorDepositoCobrado = new BigDecimal("1.50");

        resultado = doubleValorAplicado - resultado;
        resultado = resultado - new Double(valorDepositoCobrado.toString());

        String retorno = df.format(resultado).replace(",", ".");

        return new BigDecimal(retorno);

    }

    /**
     *
     * @param valorAplicado
     * @return
     */
    public static BigDecimal calcularTaxaSaqueEDepositoBitCoinToYou(BigDecimal valorAplicado) {

        DecimalFormat df = new DecimalFormat("#.##");

        Double TAXA_DEPOSITO = 0.0189d;

        Double doubleValorAplicado = new Double(valorAplicado.toString());
        Double resultado = (doubleValorAplicado * TAXA_DEPOSITO);

        resultado = doubleValorAplicado - resultado;

        String retorno = df.format(resultado).replace(",", ".");

        return new BigDecimal(retorno);

    }

}
