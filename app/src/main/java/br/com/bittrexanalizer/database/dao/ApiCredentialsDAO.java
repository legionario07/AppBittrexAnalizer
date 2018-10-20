package br.com.bittrexanalizer.database.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import br.com.bittrexanalizer.api.ApiCredentials;
import br.com.bittrexanalizer.database.bd.ConnectionFactory;

/**
 * Created by PauLinHo on 24/06/2017.
 */

public class ApiCredentialsDAO implements IDAO<ApiCredentials> {

    private SQLiteDatabase conn = null;
    private Context context = null;

    public ApiCredentialsDAO(Context context) {
        this.context = context;
    }


    @Override
    public Long create(ApiCredentials p) {

        //Sql de Inserção no BD
        StringBuffer sql = new StringBuffer();
        sql.append("insert into API_CREDENTIALS ");
        sql.append("(key, secret) ");
        sql.append("values ( ?, ?)");

        Long iResult = null;

        //abrindo a conexao com o Banco de DAdos
        if (conn == null || !conn.isOpen()) {
            conn = ConnectionFactory.getConnection(context);
        }

        try {
            SQLiteStatement pstm = conn.compileStatement(sql.toString());
            int i = 0;

            pstm.bindString(++i, p.getKey());
            pstm.bindString(++i, p.getSecret());


            iResult = pstm.executeInsert();

        } catch (Exception ex) {
            Log.i("LOG", "ERRO AO REALIZAR INSERÇÃO NO BD: " + ex.getMessage());
        }

        conn.close();

        return iResult;
    }


    @Override
    public long update(ApiCredentials p) {

        long retorno = 0;

        //Sql de Inserção no BD
        StringBuffer sql = new StringBuffer();
        sql.append("update API_CREDENTIALS ");
        sql.append("set key = ?, secret = ? ");
        sql.append("where id = ?");

        //abrindo a conexao com o Banco de DAdos
        if (conn == null || !conn.isOpen()) {
            conn = ConnectionFactory.getConnection(context);
        }

        try {
            SQLiteStatement pstm = conn.compileStatement(sql.toString());
            int i = 0;
            pstm.bindString(++i, p.getKey());
            pstm.bindString(++i, p.getSecret());
            pstm.bindLong(++i, p.getId());

            retorno = pstm.executeUpdateDelete();


        } catch (Exception ex) {
            Log.i("LOG", "ERRO AO REALIZAR UPDATE NO BD: " + ex.getMessage());
            retorno = 0l;
        }

        conn.close();
        return retorno;
    }


    @Override
    public void delete(ApiCredentials p) {

        try {
            StringBuffer sql = new StringBuffer();
            sql.append("delete from API_CREDENTIALS where id = ?");

            //Verifica se a connection é null
            if (conn == null || !conn.isOpen()) {
                conn = ConnectionFactory.getConnection(context);
            }

            SQLiteStatement stm = conn.compileStatement(sql.toString());
            stm.bindLong(1, p.getId());

            stm.executeUpdateDelete();

        } catch (Exception e) {
            Log.i("BITTREX", e.getMessage());
        }

        conn.close();

    }

    @Override
    public ApiCredentials find(ApiCredentials j) {

        //sql de select para o BD
        StringBuffer sql = new StringBuffer();
        sql.append("select * from API_CREDENTIALS where id = ?");

        ApiCredentials p = null;

        //abrindo a conexao com o Banco de DAdos
        if (conn == null || !conn.isOpen()) {
            conn = ConnectionFactory.getConnection(context);
        }
        try {
            //Cursor que recebe todos as entidades cadastradas
            String[] arg = {String.valueOf(j.getId())};
            Cursor cursor = conn.rawQuery(sql.toString(), arg);

            //Se houver primeiro mova para ele
            if (cursor.moveToFirst()) {
                do {
                    p = new ApiCredentials();
                    //recebendo os dados do banco de dados e armazenando do dominio contato
                    int i = 0;

                    p.setId(cursor.getLong(i));
                    p.setKey(cursor.getString(++i));
                    p.setSecret(cursor.getString(++i));


                } while (cursor.moveToNext()); //se existir proximo mova para ele
            }

        } catch (Exception e) {
            Log.i("BITTREX", e.getMessage());
        }

        conn.close();
        return p;
    }


    @Override
    public synchronized Set<ApiCredentials> findAll() {

        Set<ApiCredentials> lista = new HashSet<>();

        //abrindo a conexao com o Banco de DAdos
        if (conn == null || !conn.isOpen()) {
            conn = ConnectionFactory.getConnection(context);
        }

        //sql de select para o BD
        StringBuffer sql = new StringBuffer();
        sql.append("select * from API_CREDENTIALS order by id asc");

        //Cursor que recebe todas as entidades cadastradas
        Cursor cursor = conn.rawQuery(sql.toString(), null);

        //Existe Dados?
        if (cursor.moveToFirst()) {
            do {

                //recebendo os dados do banco de dados e armazenando do dominio contato
                ApiCredentials p = new ApiCredentials();
                int i = 0;

                p.setId(cursor.getLong(i));
                p.setKey(cursor.getString(++i));
                p.setSecret(cursor.getString(++i));
                //add a entidade na lista
                lista.add(p);


            } while (cursor.moveToNext()); //se existir proximo mova para ele
        }

        cursor.close();
        conn.close();

        return lista;
    }

    public synchronized LinkedList<ApiCredentials> all() {

        LinkedList<ApiCredentials> lista = new LinkedList<>();

        //abrindo a conexao com o Banco de DAdos
        if (conn == null || !conn.isOpen()) {
            conn = ConnectionFactory.getConnection(context);
        }

        //sql de select para o BD
        StringBuffer sql = new StringBuffer();
        sql.append("select * from API_CREDENTIALS order by id asc");

        //Cursor que recebe todas as entidades cadastradas
        Cursor cursor = conn.rawQuery(sql.toString(), null);

        //Existe Dados?
        if (cursor.moveToFirst()) {
            do {

                //recebendo os dados do banco de dados e armazenando do dominio contato
                ApiCredentials p = new ApiCredentials();
                int i = 0;

                p.setId(cursor.getLong(i));
                p.setKey(cursor.getString(++i));
                p.setSecret(cursor.getString(++i));
                //add a entidade na lista
                lista.add(p);


            } while (cursor.moveToNext()); //se existir proximo mova para ele
        }

        cursor.close();
        conn.close();

        return lista;
    }

}
