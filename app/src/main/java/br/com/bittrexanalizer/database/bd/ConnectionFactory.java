package br.com.bittrexanalizer.database.bd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by PauLinHo on 24/06/2017.
 */

/**
 * Retonar uma connection válida do BD
 */
public class ConnectionFactory {

    public static SQLiteDatabase getConnection(Context context){
        BittrexBD bittrexBD = new BittrexBD(context);
        SQLiteDatabase conn = null;

        try{
            conn = bittrexBD.getWritableDatabase();
        }catch (Exception e){
            System.out.print(e.getMessage());
            Log.i("BITTREX", "NÃO FOI POSSÍVEL CONECTAR AO BANCO DE DADOS: "+ e.getMessage());
        }

        return conn;
    }
}
