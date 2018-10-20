package br.com.bittrexanalizer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.bittrexanalizer.R;
import br.com.bittrexanalizer.domain.Ticker;

/**
 * Created by PauLinHo on 08/10/2017.
 */

public class AdapterExchanges extends ArrayAdapter<Ticker> {

    private List<Ticker> lista;
    private Context context;

    public AdapterExchanges(Context context, List<Ticker> lista) {
        super(context, 0, lista);
        this.context = context;
        this.lista = new ArrayList<>();
        this.lista = lista;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Ticker ticker = new Ticker();
        ticker = lista.get(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.activity_item_ticker, null);

        TextView txtNomeMoeda = (TextView) convertView.findViewById(R.id.txtNomeMoeda);
        TextView txtSiglaMoeda = (TextView) convertView.findViewById(R.id.txtSiglaMoeda);
        TextView txtLast = (TextView) convertView.findViewById(R.id.txtLast);
        TextView txtBid = (TextView) convertView.findViewById(R.id.txtBid);
        TextView txtAsk = (TextView) convertView.findViewById(R.id.txtAsk);

        txtNomeMoeda.setText(ticker.getNomeExchange());
        txtSiglaMoeda.setText(ticker.getSigla());
        txtLast.setText(ticker.getLast().toString());
        txtBid.setText(ticker.getBid().toString());
        txtAsk.setText(ticker.getAsk().toString());

        return convertView;
    }

}
