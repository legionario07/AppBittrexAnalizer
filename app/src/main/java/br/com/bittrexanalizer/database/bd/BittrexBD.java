package br.com.bittrexanalizer.database.bd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by PauLinHo on 24/06/2017.
 */

/**
 * Classe que representa o BD do projeto
 */
public class BittrexBD extends SQLiteOpenHelper {

    private static Integer VERSION_BD = 8;

    public BittrexBD(Context context){

        super(context, "bittrexBD",null, VERSION_BD);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(ScriptDropTable.excluirTabelaTICKER());
        db.execSQL(ScriptDropTable.excluirTabelaCONFIGURACAO());
        db.execSQL(ScriptDropTable.excluirTabelaAPICREDENTIALS());

        db.execSQL(ScriptCreateTable.criarTabelaTICKER());
        db.execSQL(ScriptCreateTable.criarTabelaCONFIGURACAO());
        db.execSQL(ScriptCreateTable.criarTabelaAPICREDENTIALS());

        Log.i("BITTREX", "Connection create with success in onCREATE");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(ScriptDropTable.excluirTabelaTICKER());
        db.execSQL(ScriptDropTable.excluirTabelaCONFIGURACAO());
        db.execSQL(ScriptDropTable.excluirTabelaAPICREDENTIALS());

        db.execSQL(ScriptCreateTable.criarTabelaTICKER());
        db.execSQL(ScriptCreateTable.criarTabelaCONFIGURACAO());
        db.execSQL(ScriptCreateTable.criarTabelaAPICREDENTIALS());

        Log.i("BITTREX", "Connection create with success in onUPDATE");
    }
}
