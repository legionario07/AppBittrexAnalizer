package br.com.bittrexanalizer.telas;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;

import br.com.bittrexanalizer.R;
import br.com.bittrexanalizer.adapters.AdapterOpenOrders;
import br.com.bittrexanalizer.domain.Order;
import br.com.bittrexanalizer.strategy.CancelOrderStrategy;
import br.com.bittrexanalizer.strategy.IStrategy;
import br.com.bittrexanalizer.strategy.OpenOrderStrategy;
import br.com.bittrexanalizer.utils.ConstantesUtil;
import br.com.bittrexanalizer.utils.EmailUtil;
import br.com.bittrexanalizer.utils.SessionUtil;

/**
 * Created by PauLinHo on 17/01/2018.
 */

public class UserOpenOrders extends Activity implements IFlagment {

    private ListView lstOrders;
    private LinkedList<Order> orders;
    private AdapterOpenOrders adapterOrders;
    private SwipeRefreshLayout swipeRefreshMain;
    private Handler handler = new Handler();
    private AlertDialog alertDialog;
    private EmailUtil emailUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_orders);

        lstOrders = findViewById(R.id.lstOpenOrders);
        swipeRefreshMain = findViewById(R.id.swipeRefleshMain);

        swipeRefreshMain.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOrders();
                swipeRefreshMain.setRefreshing(false);
            }
        });

        swipeRefreshMain.setColorSchemeResources(
                R.color.colorAccent,
                R.color.colorPrimary,
                R.color.colorPrimaryDark
        );

        getOrders();

        lstOrders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Order order = (Order) lstOrders.getItemAtPosition(position);

                if (order != null) {
                    showDialogCancelOrder(order);
                } else {
                    Toast.makeText(UserOpenOrders.this, "Erro ao Abrir a Order", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void showDialogCancelOrder(final Order order) {

        AlertDialog.Builder alert = new AlertDialog.Builder(UserOpenOrders.this);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_cancel_order, null);
        alert.setView(dialogView);

        final TextView txtCurrency =  dialogView.findViewById(R.id.txtCurrency);
        final TextView txtQuantity =  dialogView.findViewById(R.id.txtQuantity);
        final TextView txtOpened =  dialogView.findViewById(R.id.txtOpened);
        final TextView txtCondition =  dialogView.findViewById(R.id.txtCondition);
        final TextView txtOrderType =  dialogView.findViewById(R.id.txtOrderType);


        txtCurrency.setText(order.getExchange());
        txtQuantity.setText(order.getQuantity().toString());
        txtOpened.setText(IStrategy.SDF_DDMMYYYY_HHMMSS.format(order.getOpened().getTime()));
        txtCondition.setText(order.getCondition());
        txtOrderType.setText(order.getOrderType());

        alert.setPositiveButton("CANCELAR ORDER", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final boolean hasCanceled = CancelOrderStrategy.execute(order);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                    Toast.makeText(UserOpenOrders.this,
                                            "Operação executada", Toast.LENGTH_SHORT).show();

                                    emailUtil = new EmailUtil();
                                    emailUtil.enviarEmail(UserOpenOrders.this, ConstantesUtil.CANCEl_ORDER,
                                            "Order: UUId"+order.getOrderUuid()+
                                    " - Exchange: "+order.getExchange()+ " - VALOR: "+ order.getLimit() +
                                    " \nCancelada com sucesso", "CANCEL_ORDER");

                            }
                        });

                        recreate();

                    }
                }
        )


                .setNegativeButton("SAIR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        return;
                    }
                });

        alertDialog = alert.create();
        alertDialog.setIcon(R.drawable.ic_delete_black_24dp);
        alertDialog.show();
    }

    private void getOrders() {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                orders = new LinkedList<>();

                boolean foiLocalizado = OpenOrderStrategy.execute();

                if(!foiLocalizado && SessionUtil.getInstance().getMapOpenOrders()==null){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UserOpenOrders.this, "Ocorreu um erro", Toast.LENGTH_LONG).show();
                        }
                    });
                }else{
                    for (Order order : SessionUtil.getInstance().getMapOpenOrders().values()) {
                        orders.add(order);
                    }
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (orders.size() == 0) {
                            Toast.makeText(UserOpenOrders.this, "Sem dados para Exibição", Toast.LENGTH_LONG).show();
                        } else {
                            atualizarListView();
                        }

                    }
                });
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void atualizarListView() {

        if (adapterOrders == null) {
            adapterOrders = new AdapterOpenOrders(UserOpenOrders.this, orders);
            lstOrders.setAdapter(adapterOrders);
        } else {
            adapterOrders.clear();
            adapterOrders.notifyDataSetChanged();
            adapterOrders.addAll(orders);
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
