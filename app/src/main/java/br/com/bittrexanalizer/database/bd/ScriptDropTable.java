package br.com.bittrexanalizer.database.bd;

import android.util.Log;

/**
 * Created by PauLinHo on 24/06/2017.
 */

public class ScriptDropTable {


    public static String excluirTabelaTICKER() {

        StringBuffer sql = new StringBuffer();
        sql.append("DROP TABLE IF EXISTS TICKER");

        Log.i("BITTREX", sql.toString());

        return sql.toString();
    }

    public static String excluirTabelaCONFIGURACAO() {

        StringBuffer sql = new StringBuffer();
        sql.append("DROP TABLE IF EXISTS CONFIGURACAO");

        Log.i("BITTREX", sql.toString());

        return sql.toString();
    }

    public static String excluirTabelaAPICREDENTIALS() {

        StringBuffer sql = new StringBuffer();
        sql.append("DROP TABLE IF EXISTS API_CREDENTIALS");

        Log.i("BITTREX", sql.toString());

        return sql.toString();
    }

    public static String excluirTabelaORDER() {

        StringBuffer sql = new StringBuffer();
        sql.append("DROP TABLE IF EXISTS ORDER");

        Log.i("BITTREX", sql.toString());

        return sql.toString();
    }

}


