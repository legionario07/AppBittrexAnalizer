package br.com.bittrexanalizer.analises;

import java.util.LinkedList;

import br.com.bittrexanalizer.domain.Candle;
import br.com.bittrexanalizer.domain.EntidadeDomain;

/**
 * Created by PauLinHo on 12/01/2018.
 */

public interface IAnaliser<T extends EntidadeDomain> {

    int ERRO = -2;
    int IDEAL_PARA_VENDA = -1;
    int SEM_OSCILACAO = 0;
    int IDEAL_PARA_COMPRA = 1;
    String COMPRA = "COMPRA";
    String VENDA = "VENDA";

    /**
     * Executa a verificação na Analize escolhida
     * @return -2 ERRO
     *         -1 IDEAL PARA VENDA
     *          0 SEM OSCILAÇÂO
     *          1 IDEAL PARA COMPRA
     */
    int analizer(LinkedList<Candle> candles);


}
