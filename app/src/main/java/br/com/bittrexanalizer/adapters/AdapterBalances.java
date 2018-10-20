package br.com.bittrexanalizer.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.bittrexanalizer.R;
import br.com.bittrexanalizer.domain.Balance;


/**
 * Created by PauLinHo on 10/08/2017.
 */

public class AdapterBalances extends ArrayAdapter<Balance> {

    private Context context;
    private List<Balance> lista;

    public AdapterBalances(Context context, List<Balance> lista) {
        super(context, 0, lista);
        this.context = context;
        this.lista = new ArrayList<>();
        this.lista = lista;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        Balance balance = new Balance();
        balance = this.lista.get(position);

        convertView = LayoutInflater.from(this.context).inflate(R.layout.activity_item_balance, null);

        if(position%2==0){
            convertView.setBackgroundColor(Color.LTGRAY);
        }else{
            convertView.setBackgroundColor(Color.WHITE);
        }

        TextView txtCurrency = convertView.findViewById(R.id.txtCurrency);
        TextView txtBalance = convertView.findViewById(R.id.txtBalance);
        TextView txtAvailable = convertView.findViewById(R.id.txtAvailable);
        TextView txtPending = convertView.findViewById(R.id.txtPending);


        txtCurrency.setText(balance.getCurrency());
        txtBalance.setText(balance.getBalance().toString());
        txtAvailable.setText(balance.getAvailable().toString());
        txtPending.setText(balance.getPending().toString());

        return convertView;

    }

}
