package br.com.bittrexanalizer.utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by PauLinHo on 27/07/2017.
 */

/**
 * Strategy verifica a conexão com a internet
 */
public class VerificaConexaoStrategy {

    /**
     * Verifica se há conexão com a internet
     * @param context
     * @return - true se houver conexão
     */
    public static boolean verificarConexao(Context context) {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;
        } else {
            conectado = false;
        }
        return conectado;
    }

}
