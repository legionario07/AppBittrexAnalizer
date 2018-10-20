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

public class AdapterOrder extends ArrayAdapter<Order> {

    private Context context;
    private List<Order> lista;

    public AdapterOrder(Context context, List<Order> lista) {
        super(context, 0, lista);
        this.context = context;
        this.lista = new ArrayList<>();
        this.lista = lista;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        Order order = new Order();
        order = this.lista.get(position);

        convertView = LayoutInflater.from(this.context).inflate(R.layout.activity_item_orders, null);

        if(position%2==0){
            convertView.setBackgroundColor(Color.LTGRAY);
        }else{
            convertView.setBackgroundColor(Color.WHITE);
        }

        TextView txtExchange = convertView.findViewById(R.id.txtExchange);
        TextView txtOrderType = convertView.findViewById(R.id.txtOrderType);
        TextView txtQuantity = convertView.findViewById(R.id.txtQuantity);
        TextView txtTimeStamp = convertView.findViewById(R.id.txtTimeStamp);
        TextView txtPrice = convertView.findViewById(R.id.txtPrice);


        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        txtExchange.setText(order.getExchange());
        txtOrderType.setText(order.getOrderType());
        txtQuantity.setText(order.getQuantity().toString());
        txtPrice.setText(order.getPrice().toString());
        txtTimeStamp.setText(sdf.format(order.getTimeStamp().getTime()));


        return convertView;

    }

}
