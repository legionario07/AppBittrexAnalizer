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
import br.com.bittrexanalizer.api.ApiCredentials;


/**
 * Created by PauLinHo on 10/08/2017.
 */

public class AdapterKeys extends ArrayAdapter<ApiCredentials> {

    private Context context;
    private List<ApiCredentials> lista;

    public AdapterKeys(Context context, List<ApiCredentials> lista) {
        super(context, 0, lista);
        this.context = context;
        this.lista = new ArrayList<>();
        this.lista = lista;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ApiCredentials apiCredentials = new ApiCredentials();
        apiCredentials = this.lista.get(position);

        convertView = LayoutInflater.from(this.context).inflate(R.layout.activity_item_key, null);

        TextView txtID = convertView.findViewById(R.id.txtId);
        TextView txtKey = convertView.findViewById(R.id.txtKey);
        TextView txtSecret = convertView.findViewById(R.id.txtSecret);


        txtID.setText(apiCredentials.getId().toString());
        txtKey.setText(apiCredentials.getKey());
        txtSecret.setText(apiCredentials.getSecret());

        return convertView;
    }

}
