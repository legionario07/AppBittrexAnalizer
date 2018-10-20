package br.com.bittrexanalizer.telas;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.LinkedList;

import br.com.bittrexanalizer.R;
import br.com.bittrexanalizer.adapters.AdapterOrder;
import br.com.bittrexanalizer.api.EncryptionUtility;
import br.com.bittrexanalizer.domain.Order;
import br.com.bittrexanalizer.strategy.OrderStrategy;
import br.com.bittrexanalizer.utils.WebServiceUtil;

/**
 * Created by PauLinHo on 17/01/2018.
 */

public class UserOrders extends Activity implements IFlagment {

    private ListView lstOrders;
    private LinkedList<Order> orders;
    private AdapterOrder adapterOrders;
    private SwipeRefreshLayout swipeRefreshMain;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        lstOrders = findViewById(R.id.lstOrders);
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

              if(order!=null) {
                  Intent i = new Intent(UserOrders.this, UserOrder.class);
                  Bundle b =  new Bundle();
                  b.putSerializable("ORDER", order);
                  i.putExtras(b);
                  startActivity(i);
              }else{
                  Toast.makeText(UserOrders.this, "Erro ao Abrir a Order", Toast.LENGTH_SHORT).show();
              }

            }
        });

    }

    private void getOrders() {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                String url = WebServiceUtil.getUrlOrderHistory();

                if (url.length() < 1) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UserOrders.this, "Ocorreu um erro. Verifique se existem Chaves cadastradas", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }

                String hash = EncryptionUtility.calculateHash(url, "HmacSHA512");

                String dados = br.com.bittrexanalizer.webserver.HttpClient.find(url, hash);

                if (!WebServiceUtil.verificarRetorno(dados)) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UserOrders.this, "Ocorreu um erro", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    orders = new OrderStrategy().getObjects(dados);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (orders.size() == 0) {
                            Toast.makeText(UserOrders.this, "Sem dados para exibição", Toast.LENGTH_LONG).show();
                        } else {
                            atualizarListView();
                        }

                    }
                });
            }
        });
        t.start();


    }

    @Override
    public void atualizarListView() {

        if (adapterOrders == null) {
            adapterOrders = new AdapterOrder(UserOrders.this, orders);
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
