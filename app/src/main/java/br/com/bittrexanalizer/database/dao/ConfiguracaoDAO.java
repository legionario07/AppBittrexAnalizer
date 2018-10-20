package br.com.bittrexanalizer.database.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.LinkedList;
import java.util.Set;

import br.com.bittrexanalizer.database.bd.ConnectionFactory;
import br.com.bittrexanalizer.domain.Configuracao;

/**
 * Created by PauLinHo on 24/06/2017.
 */

public class ConfiguracaoDAO implements IDAO<Configuracao> {

    private SQLiteDatabase conn = null;
    private Context context = null;

    public ConfiguracaoDAO(Context context) {
        this.context = context;
    }


    @Override
    public Long create(Configuracao p) {

        //Sql de Inserção no BD
        StringBuffer sql = new StringBuffer();
        sql.append("insert into CONFIGURACAO ");
        sql.append("(propriedade, valor) ");
        sql.append("values ( ?, ?)");

        Long iResult = null;

        //abrindo a conexao com o Banco de DAdos
        if (conn == null || !conn.isOpen()) {
            conn = ConnectionFactory.getConnection(context);
        }

        try {
            SQLiteStatement pstm = conn.compileStatement(sql.toString());
            int i = 0;

            pstm.bindString(++i, p.getPropriedade().toUpperCase());
            pstm.bindString(++i, p.getValor());


            iResult = pstm.executeInsert();

        } catch (Exception ex) {
            Log.i("LOG", "ERRO AO REALIZAR INSERÇÃO NO BD: " + ex.getMessage());
        }

        conn.close();

        return iResult;
    }


    @Override
    public long update(Configuracao p) {

        long retorno = 0;

        //Sql de Inserção no BD
        StringBuffer sql = new StringBuffer();
        sql.append("update CONFIGURACAO ");
        sql.append("set propriedade = ?, valor = ? ");
        sql.append("where id = ?");

        //abrindo a conexao com o Banco de DAdos
        if (conn == null || !conn.isOpen()) {
            conn = ConnectionFactory.getConnection(context);
        }

        try {
            SQLiteStatement pstm = conn.compileStatement(sql.toString());
            int i = 0;
            pstm.bindString(++i, p.getPropriedade().toUpperCase());
            pstm.bindString(++i, p.getValor());
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
    public void delete(Configuracao p) {

        try {
            StringBuffer sql = new StringBuffer();
            sql.append("delete from CONFIGURACAO where propriedade = ?");

            //Verifica se a connection é null
            if (conn == null || !conn.isOpen()) {
                conn = ConnectionFactory.getConnection(context);
            }

            SQLiteStatement stm = conn.compileStatement(sql.toString());
            stm.bindString(1, p.getPropriedade());

            stm.executeUpdateDelete();

        } catch (Exception e) {
            Log.i("BITTREX", e.getMessage());
        }

        conn.close();

    }

    @Override
    public Configuracao find(Configuracao j) {

        //sql de select para o BD
        StringBuffer sql = new StringBuffer();
        sql.append("select * from CONFIGURACAO where propriedade = ?");

        Configuracao p = null;

        //abrindo a conexao com o Banco de DAdos
        if (conn == null || !conn.isOpen()) {
            conn = ConnectionFactory.getConnection(context);
        }
        try {
            //Cursor que recebe todos as entidades cadastradas
            String[] arg = {String.valueOf(j.getPropriedade())};
            Cursor cursor = conn.rawQuery(sql.toString(), arg);

            //Se houver primeiro mova para ele
            if (cursor.moveToFirst()) {
                do {
                    p = new Configuracao();
                    //recebendo os dados do banco de dados e armazenando do dominio contato
                    int i = 0;

                    p.setId(cursor.getLong(i));
                    p.setPropriedade(cursor.getString(++i));
                    p.setValor(cursor.getString(++i));


                } while (cursor.moveToNext()); //se existir proximo mova para ele
            }

        } catch (Exception e) {
            Log.i("BITTREX", e.getMessage());
        }finally {
            conn.close();
        }


        return p;
    }

    @Override
    public Set<Configuracao> findAll() {
        return null;
    }


    public LinkedList<Configuracao> all() {

        LinkedList<Configuracao> lista = new LinkedList<>();

        //abrindo a conexao com o Banco de DAdos
        if (conn == null || !conn.isOpen()) {
            conn = ConnectionFactory.getConnection(context);
        }

        //sql de select para o BD
        StringBuffer sql = new StringBuffer();
        sql.append("select * from CONFIGURACAO order by id asc");

        //Cursor que recebe todas as entidades cadastradas
        Cursor cursor = conn.rawQuery(sql.toString(), null);

        //Existe Dados?
        if (cursor.moveToFirst()) {
            do {

                //recebendo os dados do banco de dados e armazenando do dominio contato
                Configuracao p = new Configuracao();
                int i = 0;

                p.setId(cursor.getLong(i));
                p.setPropriedade(cursor.getString(++i));
                p.setValor(cursor.getString(++i));
                //add a entidade na lista
                lista.add(p);


            } while (cursor.moveToNext()); //se existir proximo mova para ele
        }

        conn.close();

        return lista;
    }

}
