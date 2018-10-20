package br.com.bittrexanalizer.utils;

import java.math.BigDecimal;

/**
 * Created by PauLinHo on 04/02/2018.
 */

public class CalculoUtil {

    private static final Double TAXA = 0.0025;

    public static BigDecimal getPorcentagemLoss(BigDecimal valorAplicado){

        BigDecimal porcentagem = new BigDecimal(SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.STOP_LOSS));

        return getPorcentagem(valorAplicado, porcentagem);
    }

    public static BigDecimal getPorcentagemLimit(BigDecimal valorAplicado){

        BigDecimal porcentagem = new BigDecimal(SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.STOP_GAIN));

        return getPorcentagem(valorAplicado, porcentagem);
    }

    /**
     * Calcula a porcentagem, baseada no valor descontado pelo BITTREX
     * @param valorAplicado - Valor em BigDecimal aplicado
     * @param porcentagem - Porcentagem esperada
     * @return - Resultado da operacao
     */
    public static BigDecimal getPorcentagem(BigDecimal valorAplicado, BigDecimal porcentagem){

        valorAplicado = valorAplicado.add(valorAplicado.multiply(new BigDecimal(TAXA)));

        BigDecimal aux = valorAplicado.multiply(porcentagem).setScale(8, BigDecimal.ROUND_HALF_EVEN);
        aux = aux.divide(new BigDecimal("100"), BigDecimal.ROUND_HALF_EVEN);

        return aux;

    }

    /**
     * Calcula a quantidade em moeda que será obtido comprando em determinado valor,
     * por determinada quantia de BTC
     * @param quantidadeBTC - quantidade em BTC
     * @param valorMoeda - Valor da Moeda a ser Comprada
     * @return - Um BigDecimal com a quantidade
     */
    public static BigDecimal getQuantidadeASerComprada(BigDecimal quantidadeBTC, BigDecimal valorMoeda){

        BigDecimal resultado = new BigDecimal("0").setScale(8);

        //retira do quantidade de BTC a taxa
        quantidadeBTC = quantidadeBTC.subtract(
                            (quantidadeBTC.multiply(new BigDecimal(TAXA)))
                                    .setScale(8, BigDecimal.ROUND_HALF_EVEN));

        //divide pelo valor da moeda
        resultado = quantidadeBTC.divide(valorMoeda,BigDecimal.ROUND_HALF_EVEN);


        return resultado;

    }



}
