package br.com.bittrexanalizer.telas;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.LinkedList;

import br.com.bittrexanalizer.R;
import br.com.bittrexanalizer.domain.Order;

/**
 * Created by PauLinHo on 17/01/2018.
 */

public class UserOrder extends Activity implements IFlagment {

    private LinkedList<Order> orders;
    private Order order;
    private String UUID;
    private SwipeRefreshLayout swipeRefreshMain;
    private Handler handler = new Handler();

    private TextView txtOrderUuid;
    private TextView txtExchange;
    private TextView txtTimeStamp;
    private TextView txtOrderType;
    private TextView txtLimit;
    private TextView txtQuantity;
    private TextView txtQuantityRemaining;
    private TextView txtComission;
    private TextView txtPrice;
    private TextView txtPricePerUnit;
    private TextView txtIsConditional;
    private TextView txtCondition;
    private TextView txtConditionTarget;
    private TextView txtImmediateOrCancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_order);

        txtExchange = findViewById(R.id.txtExchange);
        txtOrderType = findViewById(R.id.txtOrderType);
        txtOrderUuid= findViewById(R.id.txtOrderUuid);
        txtTimeStamp = findViewById(R.id.txtTimeStamp);
        txtQuantity = findViewById(R.id.txtQuantity);
        txtQuantityRemaining = findViewById(R.id.txtQuantityRemaining);
        txtLimit = findViewById(R.id.txtLimit);
        txtComission = findViewById(R.id.txtComission);
        txtIsConditional = findViewById(R.id.txtIsConditional);
        txtCondition = findViewById(R.id.txtCondition);
        txtConditionTarget = findViewById(R.id.txtConditionTarget);
        txtImmediateOrCancel = findViewById(R.id.txtImmediateOrCancel);
        txtPrice = findViewById(R.id.txtPrice);
        txtPricePerUnit = findViewById(R.id.txtPricePerUnit);

        order = new Order();
        //Pega o numero do UUID recebido na Intent
        order = (Order) getIntent().getExtras().get("ORDER");

        if(order==null){
            Toast.makeText(this, "Erro ao Executar o Comando", Toast.LENGTH_SHORT).show();
        }

        preencherDados(order);

    }


    /**
     * Preenche dos dados da Order na View
     * @param order
     */
    private void preencherDados(Order order) {


        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        txtExchange.setText(order.getExchange());
        txtOrderType.setText(order.getOrderType());
        txtTimeStamp.setText(sdf.format(order.getTimeStamp().getTime()));
        txtOrderUuid.setText(order.getOrderUuid());
        txtLimit.setText(order.getLimit().toString());
        txtQuantity.setText(order.getQuantity().toString());
        txtQuantityRemaining.setText(order.getQuantityRemaining().toString());
        txtComission.setText(order.getComission().toString());
        txtPrice.setText(order.getPrice().toString());
        txtPricePerUnit.setText(order.getPricePerUnit().toString());
        txtIsConditional.setText(order.getIsConditional().toString());
        txtCondition.setText(order.getCondition().toString());
        txtConditionTarget.setText(order.getConditionTarget().toString());
        txtImmediateOrCancel.setText(order.getImmediateOrCancel().toString());

    }

    @Override
    public void atualizarListView() {
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
