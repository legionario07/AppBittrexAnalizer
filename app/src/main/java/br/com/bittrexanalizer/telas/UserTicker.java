package br.com.bittrexanalizer.telas;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;

import br.com.bittrexanalizer.R;
import br.com.bittrexanalizer.database.dao.TickerDAO;
import br.com.bittrexanalizer.domain.Ticker;

/**
 * Created by PauLinHo on 08/02/2018.
 */

public class UserTicker extends Activity {

    private Ticker ticker;
    private TextView txtSigla, txtAsk,
            txtBid, txtStopLost, txtStopLimit, txtExchange,txtValorDeCompra;
    private EditText inpIsBought;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_ticker);

        ticker = (Ticker) getIntent().getSerializableExtra("TICKER");

        txtSigla = findViewById(R.id.txtSigla);
        inpIsBought = findViewById(R.id.inpIsBought);
        txtAsk = findViewById(R.id.txtAsk);
        txtBid = findViewById(R.id.txtBid);
        txtStopLost = findViewById(R.id.txtStopLoss);
        txtStopLimit = findViewById(R.id.txtStopLimit);
        txtValorDeCompra = findViewById(R.id.txtValorDeCompra);
        txtExchange = findViewById(R.id.txtNomeExchange);

        txtSigla.setText(ticker.getSigla());
        inpIsBought.setText(ticker.getBought().toString().toUpperCase());
        txtExchange.setText(ticker.getNomeExchange().toUpperCase());
        txtAsk.setText(ticker.getAsk().toString());
        txtBid.setText(ticker.getBid().toString());
        txtStopLimit.setText(ticker.getAvisoStopGain().toString());
        txtStopLost.setText(ticker.getAvisoStopLoss().toString());
        BigDecimal valorDeCompra = ticker.getValorDeCompra();

        if(valorDeCompra.compareTo(BigDecimal.ZERO)==0){
            txtValorDeCompra.setText("-");
        }else{
            txtValorDeCompra.setText(valorDeCompra.toString());
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        //Se teve alteração será salvo
        String valorIsBought = inpIsBought.getText().toString();
        String mensagem = "";
        if (!valorIsBought.toLowerCase().equals(ticker.getBought().toString().toLowerCase())) {
            TickerDAO tickerDAO = new TickerDAO(UserTicker.this);
            if (valorIsBought.toLowerCase().equals("true") ||
                    valorIsBought.toLowerCase().equals("false")) {

                ticker.setBought(new Boolean(valorIsBought));

                tickerDAO.update(ticker);
                mensagem += "Atualizado no Banco de Dados.";
            } else {
                mensagem += "Valor deve ser TRUE ou FALSE";
            }
            Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
        }

    }
}
