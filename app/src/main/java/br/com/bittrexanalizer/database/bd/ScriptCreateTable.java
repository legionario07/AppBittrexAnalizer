package br.com.bittrexanalizer.database.bd;

import android.util.Log;

/**
 * Created by PauLinHo on 24/06/2017.
 */

public class ScriptCreateTable {


    public static String criarTabelaTICKER() {


        StringBuffer sql = new StringBuffer();
        sql.append("CREATE TABLE TICKER( ");
        sql.append("_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        sql.append("nomeExchange varchar(45) not null unique, ");
        sql.append("sigla varchar(45) not null unique, ");
        sql.append("urlApi varchar(45) not null unique, ");
        sql.append("last decimal(10,8) not null , ");
        sql.append("bid decimal(10,8) not null , ");
        sql.append("ask decimal(10,8) not null , ");
        sql.append("isBought boolean not null , ");
        sql.append("avisoBuyInferior decimal(10,8) , ");
        sql.append("avisoBuySuperior decimal(10,8) , ");
        sql.append("avisoStopLoss decimal(10,8) , ");
        sql.append("avisoStopGain decimal(10,8), ");
        sql.append("valorDeCompra decimal(10,8)) ");

        Log.i("BITTREX", sql.toString());

        return sql.toString();
    }

    public static String criarTabelaORDER() {

        StringBuffer sql = new StringBuffer();
        sql.append("CREATE TABLE ORDER( ");
        sql.append("_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        sql.append("uuid varchar(45) not null unique, ");
        sql.append("orderUiid varchar(45) not null unique, ");
        sql.append("exchange varchar(45), ");
        sql.append("orderType varchar(45), ");
        sql.append("quantity decimal(10,8), ");
        sql.append("quantityRemaining decimal(10,8), ");
        sql.append("limit decimal(10,8), ");
        sql.append("comissionPaid decimal(10,8), ");
        sql.append("price decimal(10,8), ");
        sql.append("pricePerUnit decimal(10,8), ");
        sql.append("opened date, ");
        sql.append("closed date, ");
        sql.append("cancelInitiated String, ");
        sql.append("immediateOrCancel String, ");
        sql.append("isConditional String, ");
        sql.append("isConditional String, ");
        sql.append("condition String, ");
        sql.append("conditionTarget String) ");

        Log.i("BITTREX", sql.toString());

        return sql.toString();
    }

    public static String criarTabelaCONFIGURACAO() {


        StringBuffer sql = new StringBuffer();
        sql.append("CREATE TABLE CONFIGURACAO( ");
        sql.append("id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        sql.append("propriedade varchar(45) not null unique, ");
        sql.append("valor varchar(45)) ");

        Log.i("BITTREX", sql.toString());

        return sql.toString();
    }

    public static String criarTabelaAPICREDENTIALS() {


        StringBuffer sql = new StringBuffer();
        sql.append("CREATE TABLE API_CREDENTIALS( ");
        sql.append("id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        sql.append("key varchar(45) not null unique, ");
        sql.append("secret varchar(45) not null unique) ");

        Log.i("BITTREX", sql.toString());

        return sql.toString();
    }



}

