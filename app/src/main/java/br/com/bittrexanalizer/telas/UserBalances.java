package br.com.bittrexanalizer.telas;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.LinkedList;

import br.com.bittrexanalizer.R;
import br.com.bittrexanalizer.adapters.AdapterBalances;
import br.com.bittrexanalizer.domain.Balance;
import br.com.bittrexanalizer.strategy.BalanceStrategy;
import br.com.bittrexanalizer.utils.SessionUtil;

/**
 * Created by PauLinHo on 17/01/2018.
 */

public class UserBalances extends Activity implements IFlagment {

    private ListView lstBalances;
    private LinkedList<Balance> balances;
    private AdapterBalances adapterBalances;
    private SwipeRefreshLayout swipeRefreshMain;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);

        lstBalances = findViewById(R.id.lstBalances);
        swipeRefreshMain = findViewById(R.id.swipeRefleshMain);

        swipeRefreshMain.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBalances();
                swipeRefreshMain.setRefreshing(false);
            }
        });

        swipeRefreshMain.setColorSchemeResources(
                R.color.colorAccent,
                R.color.colorPrimary,
                R.color.colorPrimaryDark
        );

        getBalances();

    }

    private void getBalances() {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                balances = new LinkedList<>();

                boolean foiLocalizado = BalanceStrategy.execute();

                if(!foiLocalizado){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UserBalances.this, "Ocorreu um erro", Toast.LENGTH_LONG).show();
                        }
                    });
                }else{
                    for (Balance balance : SessionUtil.getInstance().getMapBalances().values()) {
                        balances.add(balance);
                    }
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (balances.size() == 0) {
                            Toast.makeText(UserBalances.this, "Sem dados para Exibição", Toast.LENGTH_LONG).show();
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

        if (adapterBalances == null) {
            adapterBalances = new AdapterBalances(UserBalances.this, balances);
            lstBalances.setAdapter(adapterBalances);
        } else {
            adapterBalances.clear();
            adapterBalances.notifyDataSetChanged();
            adapterBalances.addAll(balances);
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
