package br.com.bittrexanalizer.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.bittrexanalizer.R;
import br.com.bittrexanalizer.domain.Order;


/**
 * Created by PauLinHo on 10/08/2017.
 */

public class AdapterOpenOrders extends ArrayAdapter<Order> {

    private Context context;
    private List<Order> lista;

    public AdapterOpenOrders(Context context, List<Order> lista) {
        super(context, 0, lista);
        this.context = context;
        this.lista = new ArrayList<>();
        this.lista = lista;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        Order order = new Order();
        order = this.lista.get(position);

        convertView = LayoutInflater.from(this.context).inflate(R.layout.activity_item_open_orders, null);

        if(position%2==0){
            convertView.setBackgroundColor(Color.LTGRAY);
        }else{
            convertView.setBackgroundColor(Color.WHITE);
        }

        TextView txtCurrency = convertView.findViewById(R.id.txtCurrency);
        TextView txtQuantity = convertView.findViewById(R.id.txtQuantity);
        TextView txtOpened = convertView.findViewById(R.id.txtOpened);
        TextView txtCondition = convertView.findViewById(R.id.txtCondition);
        TextView txtOrderType = convertView.findViewById(R.id.txtOrderType);
        TextView txtLimit = convertView.findViewById(R.id.txtLimit);


        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        txtCurrency.setText(order.getExchange());
        txtQuantity.setText(order.getQuantity().toString());
        txtCondition.setText(order.getCondition().toString());
        txtOpened.setText(sdf.format(order.getOpened().getTime()));
        txtOrderType.setText(order.getOrderType());
        txtLimit.setText(order.getLimit().toString());

        return convertView;

    }

}
