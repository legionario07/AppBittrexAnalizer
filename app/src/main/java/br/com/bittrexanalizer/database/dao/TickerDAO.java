package br.com.bittrexanalizer.database.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import br.com.bittrexanalizer.database.bd.ConnectionFactory;
import br.com.bittrexanalizer.domain.Ticker;
import br.com.bittrexanalizer.utils.WebServiceUtil;

/**
 * Created by PauLinHo on 24/06/2017.
 */

public class TickerDAO implements IDAO<Ticker> {

    private SQLiteDatabase conn = null;
    private Context context = null;

    public TickerDAO(Context context) {
        this.context = context;
    }


    @Override
    public Long create(Ticker p) {

        //Sql de Inserção no BD
        StringBuffer sql = new StringBuffer();
        sql.append("insert into TICKER ");
        sql.append("(nomeExchange, sigla, urlApi, last, bid, ask, isBought, avisoBuyInferior, ");
        sql.append("avisoBuySuperior, avisoStopLoss, avisoStopGain, valorDeCompra ) ");
        sql.append("values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        Long iResult = null;

        //abrindo a conexao com o Banco de DAdos
        if (conn == null || !conn.isOpen()) {
            conn = ConnectionFactory.getConnection(context);
        }

        try {
            SQLiteStatement pstm = conn.compileStatement(sql.toString());
            int i = 0;

            pstm.bindString(++i, p.getNomeExchange());
            pstm.bindString(++i, p.getSigla());
            pstm.bindString(++i, WebServiceUtil.getUrl() + p.getSigla());
            pstm.bindString(++i, p.getLast().toString());
            pstm.bindString(++i, p.getBid().toString());
            pstm.bindString(++i, p.getAsk().toString());
            pstm.bindString(++i, p.getBought().toString());
            pstm.bindString(++i, p.getAvisoBuyInferior().toString());
            pstm.bindString(++i, p.getAvisoBuySuperior().toString());
            pstm.bindString(++i, p.getAvisoStopLoss().toString());
            pstm.bindString(++i, p.getAvisoStopGain().toString());
            pstm.bindString(++i, p.getValorDeCompra().toString());


            iResult = pstm.executeInsert();

        } catch (Exception ex) {
            Log.i("LOG", "ERRO AO REALIZAR INSERÇÃO NO BD: " + ex.getMessage());
        }

        conn.close();

        return iResult;
    }


    @Override
    public long update(Ticker p) {

        long retorno = 0;

        //Sql de Inserção no BD
        StringBuffer sql = new StringBuffer();
        sql.append("update ticker ");
        sql.append("set nomeExchange = ?, sigla = ?, urlApi = ?, last = ?, bid = ?, ask = ?, isBought = ?, ");
        sql.append("avisoBuyInferior = ?, avisoBuySuperior = ?, avisoStopLoss = ?, avisoStopGain = ?, valorDeCompra = ? ");
        sql.append("where _id = ?");

        //abrindo a conexao com o Banco de DAdos
        if (conn == null || !conn.isOpen()) {
            conn = ConnectionFactory.getConnection(context);
        }

        try {
            SQLiteStatement pstm = conn.compileStatement(sql.toString());
            int i = 0;
            pstm.bindString(++i, p.getNomeExchange());
            pstm.bindString(++i, p.getSigla());
            pstm.bindString(++i, WebServiceUtil.getUrl() + p.getSigla());
            pstm.bindString(++i, p.getLast().toString());
            pstm.bindString(++i, p.getBid().toString());
            pstm.bindString(++i, p.getAsk().toString());
            pstm.bindString(++i, p.getBought().toString());
            pstm.bindString(++i, p.getAvisoBuyInferior().toString());
            pstm.bindString(++i, p.getAvisoBuySuperior().toString());
            pstm.bindString(++i, p.getAvisoStopLoss().toString());
            pstm.bindString(++i, p.getAvisoStopGain().toString());
            pstm.bindString(++i, p.getValorDeCompra().toString());
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
    public void delete(Ticker p) {

        try {
            StringBuffer sql = new StringBuffer();
            sql.append("delete from TICKER where sigla = ?");

            //Verifica se a connection é null
            if (conn == null || !conn.isOpen()) {
                conn = ConnectionFactory.getConnection(context);
            }

            SQLiteStatement stm = conn.compileStatement(sql.toString());
            stm.bindString(1, p.getSigla());

            stm.executeUpdateDelete();

        } catch (Exception e) {
            Log.i("BITTREX", e.getMessage());
        }

        conn.close();

    }

    @Override
    public Ticker find(Ticker j) {

        //sql de select para o BD
        StringBuffer sql = new StringBuffer();
        sql.append("select * from TICKER where nomeExchange = ?");

        Ticker p = null;

        //abrindo a conexao com o Banco de DAdos
        if (conn == null || !conn.isOpen()) {
            conn = ConnectionFactory.getConnection(context);
        }
        try {
            //Cursor que recebe todos as entidades cadastradas
            String[] arg = {String.valueOf(j.getNomeExchange())};
            Cursor cursor = conn.rawQuery(sql.toString(), arg);

            //Se houver primeiro mova para ele
            if (cursor.moveToFirst()) {
                do {
                    p = getTicker(cursor);

                } while (cursor.moveToNext()); //se existir proximo mova para ele
            }

        } catch (Exception e) {
            Log.i("BITTREX", e.getMessage());
        }

        conn.close();
        return p;
    }

    public LinkedList<Ticker> findAllIsBought(Boolean valor) {

        //sql de select para o BD
        StringBuffer sql = new StringBuffer();
        sql.append("select * from TICKER where isBought = ?");

        LinkedList<Ticker> tickers = new LinkedList<>();

        Ticker p = null;

        //abrindo a conexao com o Banco de DAdos
        if (conn == null || !conn.isOpen()) {
            conn = ConnectionFactory.getConnection(context);
        }
        try {
            //Cursor que recebe todos as entidades cadastradas
            String[] arg = {String.valueOf(valor)};
            Cursor cursor = conn.rawQuery(sql.toString(), arg);

            //Se houver primeiro mova para ele
            if (cursor.moveToFirst()) {
                do {
                    p = getTicker(cursor);

                    tickers.add(p);

                } while (cursor.moveToNext()); //se existir proximo mova para ele
            }

        } catch (Exception e) {
            Log.i("BITTREX", e.getMessage());
        }

        conn.close();
        return tickers;
    }


    @Override
    public synchronized Set<Ticker> findAll() {

        Set<Ticker> lista = new HashSet<>();

        //abrindo a conexao com o Banco de DAdos
        if (conn == null || !conn.isOpen()) {
            conn = ConnectionFactory.getConnection(context);
        }

        //sql de select para o BD
        StringBuffer sql = new StringBuffer();
        sql.append("select * from Ticker order by sigla asc");

        //Cursor que recebe todas as entidades cadastradas
        Cursor cursor = conn.rawQuery(sql.toString(), null);

        //Existe Dados?
        if (cursor.moveToFirst()) {
            do {

                //recebendo os dados do banco de dados e armazenando do dominio contato
                Ticker p = getTicker(cursor);
                //add a entidade na lista
                lista.add(p);


            } while (cursor.moveToNext()); //se existir proximo mova para ele
        }

        conn.close();

        return lista;
    }

    public LinkedList<Ticker> findAllTickers() {

        LinkedList<Ticker> lista = new LinkedList<>();

        //abrindo a conexao com o Banco de DAdos
        if (conn == null || !conn.isOpen()) {
            conn = ConnectionFactory.getConnection(context);
        }

        //sql de select para o BD
        StringBuffer sql = new StringBuffer();
        sql.append("select * from Ticker order by sigla asc");

        //Cursor que recebe todas as entidades cadastradas
        Cursor cursor = conn.rawQuery(sql.toString(), null);

        //Existe Dados?
        if (cursor.moveToFirst()) {
            do {

                //recebendo os dados do banco de dados e armazenando do dominio contato
                Ticker p = getTicker(cursor);
                //add a entidade na lista
                lista.add(p);


            } while (cursor.moveToNext()); //se existir proximo mova para ele
        }

        conn.close();

        return lista;
    }

    public synchronized Set<Ticker> findAllByBought(Ticker j) {

        Set<Ticker> lista = new HashSet<>();

        //abrindo a conexao com o Banco de DAdos
        if (conn == null || !conn.isOpen()) {
            conn = ConnectionFactory.getConnection(context);
        }

        //sql de select para o BD
        StringBuffer sql = new StringBuffer();
        sql.append("select * from Ticker where isBought = ?");

        //Cursor que recebe todas as entidades cadastradas
        String[] arg = {String.valueOf(j.getBought())};
        Cursor cursor = conn.rawQuery(sql.toString(), null);

        //Existe Dados?
        if (cursor.moveToFirst()) {
            do {

                //recebendo os dados do banco de dados e armazenando do dominio contato
                Ticker p = getTicker(cursor);

                //add a entidade na lista
                lista.add(p);


            } while (cursor.moveToNext()); //se existir proximo mova para ele
        }

        conn.close();
        return lista;
    }

    private Ticker getTicker(Cursor cursor){

        Ticker p = new Ticker();
        int i = 0;

        p.setId(cursor.getLong(i));
        p.setNomeExchange(cursor.getString(++i));
        p.setSigla(cursor.getString(++i));
        p.setUrlApi(cursor.getString(++i));
        p.setLast(new BigDecimal(cursor.getString(++i)));
        p.setBid(new BigDecimal(cursor.getString(++i)));
        p.setAsk(new BigDecimal(cursor.getString(++i)));
        p.setBought(Boolean.valueOf(cursor.getString(++i)));
        p.setAvisoBuyInferior(new BigDecimal(cursor.getString(++i)));
        p.setAvisoBuySuperior(new BigDecimal(cursor.getString(++i)));
        p.setAvisoStopLoss(new BigDecimal(cursor.getString(++i)));
        p.setAvisoStopGain(new BigDecimal(cursor.getString(++i)));
        p.setValorDeCompra(new BigDecimal(cursor.getString(++i)));

        return p;
    }

}
