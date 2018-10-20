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
import br.com.bittrexanalizer.domain.Configuracao;


/**
 * Created by PauLinHo on 10/08/2017.
 */

public class AdapterConfiguracoes extends ArrayAdapter<Configuracao> {

    private Context context;
    private List<Configuracao> lista;

    public AdapterConfiguracoes(Context context, List<Configuracao> lista) {
        super(context, 0, lista);
        this.context = context;
        this.lista = new ArrayList<>();
        this.lista = lista;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Configuracao configuracao = new Configuracao();
        configuracao = this.lista.get(position);

        convertView = LayoutInflater.from(this.context).inflate(R.layout.activity_item_configuracao, null);

        TextView txtConfiguracaoPropriedade = (TextView) convertView.findViewById(R.id.txtConfiguracaoPropriedade);
        TextView txtConfiguracaoValor = (TextView) convertView.findViewById(R.id.txtConfiguracaoValor);


        txtConfiguracaoPropriedade.setText(configuracao.getPropriedade());
        txtConfiguracaoValor.setText(configuracao.getValor());

        return convertView;
    }

}
