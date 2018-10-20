package br.com.bittrexanalizer.telas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.com.bittrexanalizer.R;
import br.com.bittrexanalizer.adapters.AdapterExchanges;
import br.com.bittrexanalizer.api.ApiCredentials;
import br.com.bittrexanalizer.database.bd.BittrexBD;
import br.com.bittrexanalizer.database.dao.ApiCredentialsDAO;
import br.com.bittrexanalizer.database.dao.ConfiguracaoDAO;
import br.com.bittrexanalizer.database.dao.TickerDAO;
import br.com.bittrexanalizer.domain.Balance;
import br.com.bittrexanalizer.domain.Configuracao;
import br.com.bittrexanalizer.domain.Order;
import br.com.bittrexanalizer.domain.Ticker;
import br.com.bittrexanalizer.receivers.ServiceAvisoTaxa;
import br.com.bittrexanalizer.receivers.ServiceCompra;
import br.com.bittrexanalizer.receivers.ServiceVenda;
import br.com.bittrexanalizer.strategy.AlarmAnalizerCompraStrategy;
import br.com.bittrexanalizer.strategy.BalanceStrategy;
import br.com.bittrexanalizer.strategy.BuyOrderStrategy;
import br.com.bittrexanalizer.strategy.CancelOrderStrategy;
import br.com.bittrexanalizer.strategy.OpenOrderStrategy;
import br.com.bittrexanalizer.strategy.SellOrderStrategy;
import br.com.bittrexanalizer.strategy.StopOrderStrategy;
import br.com.bittrexanalizer.utils.CalculoUtil;
import br.com.bittrexanalizer.utils.ConstantesUtil;
import br.com.bittrexanalizer.utils.EmailUtil;
import br.com.bittrexanalizer.utils.SessionUtil;
import br.com.bittrexanalizer.utils.VerificaConexaoStrategy;
import br.com.bittrexanalizer.utils.WebServiceUtil;
import br.com.bittrexanalizer.webserver.HttpClient;

import static br.com.bittrexanalizer.utils.TickerComparator.ordenar;

public class MainActivityDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Runnable {

    public static MainActivityDrawer instance;

    private ListView lstTicker;
    private AdapterExchanges adapterExchanges;
    private View viewSuperior;
    private SwipeRefreshLayout swipeRefreshMain;
    private LinearLayout lnlTitulo;
    private LinearLayout lnlColunas;

    private EmailUtil emailUtil = new EmailUtil();

    private EditText inpValorAvisoBuyAbaixo, inpValorAvisoBuyAcima,
            inpValorAvisoSellAbaixo, inpValorAvisoSellAcima, inpValorDeCompra;
    private ToggleButton tbBuyAbaixo, tbBuyAcima, tbSellAbaixo, tbSellAcima;

    private Ticker ticker;
    private volatile LinkedList<Ticker> tickers;
    private TickerDAO tickerDAO;
    private BittrexBD bittrexBD;

    private Menu menu;
    private Map<String, String> mapSiglaNomeExchange;

    private TextView txtCalcular;
    private TextView txtDeletar;
    private TextView txtCriarNotificacao;
    private TextView txtCriarCompra;
    private TextView txtVender;
    private TextView txtDetalhar;
    private TextView txtCriarStop;
    private boolean hasInternet = true;

    private int qtde;

    private final Timer myTimer = new Timer();

    private ProgressDialog dialog;
    private AlertDialog alert;
    private AlertDialog alertDialog;

    private EditText inpQuantidadeDeBTCParaComprar;
    private EditText inpValorStop;

    private final Double TAXA_BUY = 0.0025;
    private final Double TAXA_SELL = 0.0025;

    private final String LOG = "BITTREX";

    private int contadorNomeClassificacao = 0;
    private int contadorLastClassificacao = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        menu = navigationView.getMenu();

        lstTicker = (ListView) findViewById(R.id.lstTickers);
        viewSuperior = (View) findViewById(R.id.viewSuperior);
        lnlTitulo = (LinearLayout) findViewById(R.id.lnlTitulo);
        lnlColunas = (LinearLayout) findViewById(R.id.lnlColunas);
        swipeRefreshMain = (SwipeRefreshLayout) findViewById(R.id.swipeRefleshMain);
        swipeRefreshMain.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTicker();
                swipeRefreshMain.setRefreshing(false);
            }
        });
        swipeRefreshMain.setColorSchemeResources(
                R.color.colorAccent,
                R.color.colorPrimary,
                R.color.colorPrimaryDark
        );

        int i = getResources().getConfiguration().orientation;
        if (i == 2) {
            viewSuperior.setLayoutParams(new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.6f));
            lnlTitulo.setLayoutParams(new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.4f));
            lnlColunas.setLayoutParams(new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.9f));
        }

        mapSiglaNomeExchange = new HashMap<>();

        bittrexBD = new BittrexBD(this);
        //bittrexBD.onCreate(ConnectionFactory.getConnection(this));
        tickerDAO = new TickerDAO(this);

        dialog = new ProgressDialog(MainActivityDrawer.this);
        dialog.setMessage("Processando...");
        dialog.setTitle("BittrexAnalizer");
        dialog.show();

        hasInternet = VerificaConexaoStrategy.verificarConexao(MainActivityDrawer.this);

        if (!hasInternet) {
            Toast.makeText(this, "Necessário conexão com a Internet", Toast.LENGTH_LONG).show();
            finish();
        }

        getConfiguracoes();
        verificarVariaveisDeSistema();
        startServiceTaxa();
        getApiCredentials();
        getTicker();

        verificarMenuService();

        lstTicker.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                ticker = new Ticker();
                ticker = (Ticker) lstTicker.getItemAtPosition(i);

                showDialogOption();

                return true;
            }
        });

        Thread t = new Thread(this);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        myTimer.scheduleAtFixedRate(new MyTask(), 0, (long) (300000));

        if (qtde > 0)
            Toast.makeText(getApplicationContext(), "Clique em cima da Moeda para ver as Opções", Toast.LENGTH_SHORT).show();

        instance = this;

    }

    /**
     * Salva valores no SharedPreferences
     *
     * @param key
     * @param value
     * @return
     */
    private boolean salvarPreferences(String key, String value) {
        SharedPreferences.Editor editor = MainActivityDrawer.this.getSharedPreferences("SERVICES", Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        return editor.commit();
    }

    private String getPreferences(String key) {
        SharedPreferences preferences = getSharedPreferences("SERVICES", MODE_PRIVATE);
        return preferences.getString(key, "false");
    }

    private void verificarMenuService() {

        boolean isStartCompraService = new Boolean(getPreferences("SERVICE_COMPRA"));
        boolean isStartVendaService = new Boolean(getPreferences("SERVICE_VENDA"));

        //verifica se está stopado o servico de compra
        if (isStartCompraService) {
            menu.getItem(2).getSubMenu().getItem().getSubMenu().getItem(0).setVisible(false);
            menu.getItem(2).getSubMenu().getItem().getSubMenu().getItem(1).setVisible(true);
        } else {
            menu.getItem(2).getSubMenu().getItem().getSubMenu().getItem(0).setVisible(true);
            menu.getItem(2).getSubMenu().getItem().getSubMenu().getItem(1).setVisible(false);
        }

        //verifica se está stopado o servico de venda
        if (isStartVendaService) {
            menu.getItem(3).getSubMenu().getItem().getSubMenu().getItem(0).setVisible(false);
            menu.getItem(3).getSubMenu().getItem().getSubMenu().getItem(1).setVisible(true);
        } else {
            menu.getItem(3).getSubMenu().getItem().getSubMenu().getItem(0).setVisible(true);
            menu.getItem(3).getSubMenu().getItem().getSubMenu().getItem(1).setVisible(false);
        }

    }

    /**
     * Start o Service de Aviso de Taxa, que possibilita a geração de Notificações
     */
    private void startServiceTaxa() {
        Intent i = new Intent(MainActivityDrawer.this, ServiceAvisoTaxa.class);
        startService(i);
    }

    private void stopServiceTaxa() {
        Intent i = new Intent(MainActivityDrawer.this, ServiceAvisoTaxa.class);
        stopService(i);
    }

    private void startServiceCompra() {
        Intent i = new Intent(MainActivityDrawer.this, ServiceCompra.class);
        startService(i);

        salvarPreferences("SERVICE_COMPRA", "true");

    }

    private void stopServiceCompra() {
        Intent i = new Intent(MainActivityDrawer.this, ServiceCompra.class);
        stopService(i);

        salvarPreferences("SERVICE_COMPRA", "false");

    }

    private void startServiceVenda() {
        Intent i1 = new Intent(MainActivityDrawer.this, ServiceVenda.class);
        startService(i1);

        salvarPreferences("SERVICE_VENDA", "true");
    }

    private void stopServiceVenda() {
        Intent i1 = new Intent(MainActivityDrawer.this, ServiceVenda.class);
        stopService(i1);

        salvarPreferences("SERVICE_VENDA", "false");
    }

    /**
     * SE NÃO EXISTIR AS PRINCIPAIS CONFIGURAÇÕES DO SISTEMA SERÁ NECESSARIO CRIAR
     */
    private void verificarVariaveisDeSistema() {


        //Configurações de notificações
        if (!SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.EMAIL)) {
            createVariavel(ConstantesUtil.EMAIL, "paulinho.legionario07@gmail.com");
        }

        if (!SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.ANALISE_LIGADA)) {
            createVariavel(ConstantesUtil.ANALISE_LIGADA, "true");
        }

        if (!SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.ROBOT_LIGADO)) {
            createVariavel(ConstantesUtil.ROBOT_LIGADO, "true");
        }

        if (!SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.VALOR_COMPRA_ROBOT)) {
            createVariavel(ConstantesUtil.VALOR_COMPRA_ROBOT, "0.00020000");
        }

        if (!SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.ROBOT_VEZES)) {
            createVariavel(ConstantesUtil.ROBOT_VEZES, "3");
        }


        if (!SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.ENVIAR_EMAIL)) {
            createVariavel(ConstantesUtil.ENVIAR_EMAIL, "true");
        }

        if (!SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.TEMPO_ANALISE_MIN)) {
            createVariavel(ConstantesUtil.TEMPO_ANALISE_MIN, "120");

            SessionUtil.getInstance().setUltimoTempoDeNotificacaoSalvo(Long.valueOf("120"));
        }

        if (!SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.QTDE_TICKERS_PESQUISA)) {
            createVariavel(ConstantesUtil.QTDE_TICKERS_PESQUISA, "90");
        }

        if (!SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.TEMPO_ESPERA_THREAD)) {
            createVariavel(ConstantesUtil.TEMPO_ESPERA_THREAD, "30000");

        }

        if (!SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.ENVIAR_NOTIFICACAO)) {
            createVariavel(ConstantesUtil.ENVIAR_NOTIFICACAO, "true");
        }

        if (!SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.ROBOT_TEMPO_ENVIO_EMAIL)) {
            createVariavel(ConstantesUtil.ROBOT_TEMPO_ENVIO_EMAIL, "7200000");
        }

        /**
         * Configurações de ANALISES
         */
        if (!SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.LONG_EMA)) {
            createVariavel(ConstantesUtil.LONG_EMA, "26");
        }
        if (!SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.SHORT_EMA)) {
            createVariavel(ConstantesUtil.SHORT_EMA, "12");
        }
        if (!SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.SIGNAL)) {
            createVariavel(ConstantesUtil.SIGNAL, "9");
        }
        if (!SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.IFR_DIAS)) {
            createVariavel(ConstantesUtil.IFR_DIAS, "14");
        }
        if (!SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.IFR_MIN)) {
            createVariavel(ConstantesUtil.IFR_MIN, "30");
        }
        if (!SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.IFR_MAX)) {
            createVariavel(ConstantesUtil.IFR_MAX, "70");
        }

        if (!SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.MACD)) {
            createVariavel(ConstantesUtil.MACD, "true");
        }
        if (!SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.IFR)) {
            createVariavel(ConstantesUtil.IFR, "true");
        }
        if (!SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.OSCILADOR_ESTOCASTICO)) {
            createVariavel(ConstantesUtil.OSCILADOR_ESTOCASTICO, "true");
        }

        if (!SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.OBV)) {
            createVariavel(ConstantesUtil.OBV, "true");
        }

        if (!SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.OBV_QTDE_FECHAMENTOS)) {
            createVariavel(ConstantesUtil.OBV_QTDE_FECHAMENTOS, "3");
        }

        if (!SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.OE_TEMPO_PERIODO_K)) {
            createVariavel(ConstantesUtil.OE_TEMPO_PERIODO_K, "14");
        }

        if (!SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.OE_TEMPO_PERIODO_D)) {
            createVariavel(ConstantesUtil.OE_TEMPO_PERIODO_D, "3");
        }

        if (!SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.OE_TAXA_MIN)) {
            createVariavel(ConstantesUtil.OE_TAXA_MIN, "20");
        }
        if (!SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.OE_TAXA_MAX)) {
            createVariavel(ConstantesUtil.OE_TAXA_MAX, "80");
        }

        /**
         * Configurações de TRADER
         */
        if (!SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.STOP_LOSS)) {
            createVariavel(ConstantesUtil.STOP_LOSS, "97");
        }
        if (!SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.STOP_GAIN)) {
            createVariavel(ConstantesUtil.STOP_GAIN, "101");
        }

        if (!SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.PERIODICIDADE)) {
            createVariavel(ConstantesUtil.PERIODICIDADE, WebServiceUtil.getTickintervalOneMin());
        }
        if (!SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.ALL_TICKERS)) {
            createVariavel(ConstantesUtil.ALL_TICKERS, "false");
        }


        getConfiguracoes();


    }

    private void createVariavel(String propriedade, String valor) {

        ConfiguracaoDAO configuracaoDAO = null;

        if (configuracaoDAO == null) {
            configuracaoDAO = new ConfiguracaoDAO(MainActivityDrawer.this);
        }

        Configuracao c = new Configuracao();
        c.setPropriedade(propriedade);
        c.setValor(valor);
        configuracaoDAO.create(c);
    }

    private void getConfiguracoes() {

        LinkedList<Configuracao> configuracoes = new LinkedList<>();
        configuracoes = new ConfiguracaoDAO(MainActivityDrawer.this).all();

        Map<String, String> mapConfiguracao = new HashMap<String, String>();

        if (configuracoes == null) {
            SessionUtil.getInstance().setMapConfiguracao(null);
            return;
        }

        for (Configuracao c : configuracoes) {
            mapConfiguracao.put(c.getPropriedade(), c.getValor());
        }

        SessionUtil.getInstance().setMapConfiguracao(mapConfiguracao);


    }

    private void getApiCredentials() {

        ApiCredentials apiKey = new ApiCredentials();
        apiKey.setId(1l);
        apiKey = new ApiCredentialsDAO(MainActivityDrawer.this).find(apiKey);

        SessionUtil.getInstance().setApiCredentials(apiKey);

    }

    private synchronized void getTickers() {

        tickers = new LinkedList<>();
        tickers = tickerDAO.findAllTickers();

        if (tickers.size() == 0) {
            return;
        }

        ExecutorService executorService = Executors.newCachedThreadPool();
        for (Ticker t : tickers) {
            ticker = t;
            ticker.setUrlApi(WebServiceUtil.getUrl() + ticker.getSigla().toLowerCase());
            executorService.execute(t);
        }

        executorService.shutdown();

        while (!executorService.isTerminated()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Atualizando a Lista do SessionUtil
        SessionUtil.getInstance().setTickers(new LinkedList<Ticker>());
        SessionUtil.getInstance().setTickers(tickers);

    }

    private void getTicker() {

        try {
            getTickers();

            qtde = tickers.size();
            Collections.sort(tickers);

            atualizarListView(tickers);

        } catch (Exception e) {
            Log.i("BITTREX", e.getMessage());
        } finally {
            dialog.dismiss();
        }

    }


    /**
     * Cria o Dialog com as opçoes Inserir, Editar e Excluir
     */
    private void showDialogOption() {

        final AlertDialog.Builder dialogCrud = new AlertDialog.Builder(MainActivityDrawer.this);

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_option_list_view, null);
        dialogCrud.setView(dialogView);
        dialogCrud.setTitle("Escolha a Opção");


        View.OnClickListener criarDialog = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtCalcular.getId() == view.getId()) {
                    showCalcularDialog(ticker);
                } else if (txtDeletar.getId() == view.getId()) {
                    showDialogDeletarMoeda();
                } else if (txtCriarNotificacao.getId() == view.getId()) {
                    showDialogCriarNotificacao();
                } else if (txtDetalhar.getId() == view.getId()) {
                    Intent i = new Intent(MainActivityDrawer.this, UserTicker.class);
                    i.putExtra("TICKER", ticker);
                    startActivity(i);
                } else if (txtCriarCompra.getId() == view.getId()) {
                    if (inpQuantidadeDeBTCParaComprar.getText().toString().length() < 0) {
                        Toast.makeText(MainActivityDrawer.this, "Valor invalido", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    criarCompra();
                } else if (txtVender.getId() == view.getId()) {
                    //Abre um dialogo para confirmar a venda
                    AlertDialog.Builder alertVenda = new AlertDialog.Builder(MainActivityDrawer.this);
                    alertVenda.setTitle("CONFIRMAR VENDA?");
                    alertVenda.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            criarVenda();
                        }
                    });
                    alertVenda.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            return;
                        }
                    });

                    AlertDialog alert = alertVenda.create();
                    alert.show();

                }


                alert.cancel();

            }


        };

        txtCalcular = dialogView.findViewById(R.id.txtCalcular);
        txtDeletar = dialogView.findViewById(R.id.txtDeletar);
        txtCriarNotificacao = dialogView.findViewById(R.id.txtCriarNotificacao);
        txtCriarCompra = dialogView.findViewById(R.id.txtCriarCompra);
        txtCriarStop = dialogView.findViewById(R.id.txtCriarStopLoss);
        txtVender = dialogView.findViewById(R.id.txtVender);
        txtDetalhar = dialogView.findViewById(R.id.txtDetalhar);

        inpQuantidadeDeBTCParaComprar = dialogView.findViewById(R.id.inpQuantidadeDeBTCParaComprar);
        inpValorStop = dialogView.findViewById(R.id.inpValorStop);

        txtCalcular.setOnClickListener(criarDialog);
        txtDeletar.setOnClickListener(criarDialog);
        txtCriarNotificacao.setOnClickListener(criarDialog);
        txtCriarStop.setOnClickListener(criarDialog);
        txtCriarCompra.setOnClickListener(criarDialog);
        txtVender.setOnClickListener(criarDialog);
        txtDetalhar.setOnClickListener(criarDialog);


        dialogCrud.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener()

        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                return;
            }
        });

        alert = dialogCrud.create();
        alert.show();
    }

    /**
     * Cria um stop de venda, deve ser menor que o valor do objeto
     */
    private synchronized void criarStop() {

        Order order = new Order();

        //Existe um stop ja?
        if (SessionUtil.getInstance().getMapOpenOrders().containsKey(ticker.getSigla())) {
            order = SessionUtil.getInstance().getMapOpenOrders().get(ticker.getSigla());

            if (order.getOrderUuid() != null) {
                CancelOrderStrategy.execute(order);
            }

        } else {
            //Atualiza o map de Balances
            BalanceStrategy.execute();

            //Não existe stop, então será pega a quantidade total da moeda disponível
            Balance b = SessionUtil.getInstance().getMapBalances().get(ticker.getSigla());
            order.setQuantity(b.getBalance());
            order.setSigla(ticker.getSigla());
        }

        //pega o valor para o stop se for vazio será usado o configuração padrão
        if (inpValorStop.getText().toString().length() < 1) {
            BigDecimal porcentagem = new BigDecimal(SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.STOP_LOSS));
            order.setRate(CalculoUtil.getPorcentagem(ticker.getBid(), porcentagem));
        } else {
            order.setRate(new BigDecimal(inpValorStop.getText().toString()));
        }

        //Executa a orderm de STOP
        boolean retorno = StopOrderStrategy.execute(order);

        //cria a mensagem do EMAIL
        final String mensagem = criarMensagemOperacao(ticker.getSigla(), order.getRate(), order.getQuantity());

        if (retorno) {
            emailUtil.enviarEmail(MainActivityDrawer.this, ConstantesUtil.COMPRA_REALIZADA,
                    mensagem, "STOP");

        } else {
            Toast.makeText(this, "Erro ao executar operação", Toast.LENGTH_SHORT).show();
        }


    }

    /**
     * Realiza uma compra com o valor atual de compra da moeda
     */
    private synchronized void criarCompra() {

        try {
            Order order = new Order();
            order.setSigla(ticker.getSigla());

            BigDecimal valor = new BigDecimal(inpQuantidadeDeBTCParaComprar.getText().toString()).setScale(8);

            //Calcula a quantidade da moeda que será comprada
            order.setQuantity(CalculoUtil.getQuantidadeASerComprada(valor, ticker.getAsk()));

            //Pega o valor atual de compra da moeda
            order.setRate(ticker.getAsk());

            //executa a compra
            boolean retorno = BuyOrderStrategy.execute(order);

            final String mensagem = criarMensagemOperacao(ticker.getSigla(), order.getRate(), order.getQuantity());

            if (retorno) {

                emailUtil.enviarEmail(MainActivityDrawer.this, ConstantesUtil.STOP_CRIADO,
                        mensagem, "COMPRA");

            } else {
                Toast.makeText(this, "Erro ao executar operação", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            new EmailUtil().enviarEmail(MainActivityDrawer.this, "ERRO", e.getMessage());

        }

    }


    /**
     * Realiza uma venda com o valor atual do ticekr
     */
    private synchronized void criarVenda() {

        try {
            Order order = new Order();

            //Atualiza as orders
            OpenOrderStrategy.execute();

            //verifica se tem uma order aberta para essa moeda
            if (SessionUtil.getInstance().getMapOpenOrders().containsKey(ticker.getSigla())) {
                order = SessionUtil.getInstance().getMapOpenOrders().get(ticker.getSigla());

                //Se tiver é necessário cancelar a ordem de stop
                if (order.getOrderUuid() != null) {
                    boolean cancelou = CancelOrderStrategy.execute(order);
                }
            } else {
                //Atualiza o map de Balances
                BalanceStrategy.execute();

                //Verifica se existe saldo para essa moeda
                if (!SessionUtil.getInstance().getMapBalances().containsKey(ticker.getSigla())) {
                    Toast.makeText(MainActivityDrawer.this, "Não há Saldo Disponível para essa moeda", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    //Se existir pega o valor
                    Balance b = SessionUtil.getInstance().getMapBalances().get(ticker.getSigla());
                    order.setQuantity(b.getBalance());
                    order.setSigla(ticker.getSigla());
                }
            }

            //Pega o valor atual de venda
            order.setRate(ticker.getBid());

            //Realiza a venda
            boolean retorno = SellOrderStrategy.execute(order);

            //cria a mensagem do email
            final String mensagem = criarMensagemOperacao(ticker.getSigla(), order.getRate(), order.getQuantity());

            if (retorno) {
                emailUtil.enviarEmail(MainActivityDrawer.this, ConstantesUtil.VENDA_REALIZADA,
                        mensagem, "VENDA");

            } else {
                Toast.makeText(this, "Erro ao executar operação", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            new EmailUtil().enviarEmail(MainActivityDrawer.this, "ERRO", e.getMessage());

        }

    }

    /**
     * Cria mensagem para enviar via email após uma operação de COMPRA, VENDA, STOP
     *
     * @param sigla      - Sigla da Moeda
     * @param valor      - Valor da Operação
     * @param quantidade - Quantidade da Moeda
     * @return
     */
    private String criarMensagemOperacao(String sigla, BigDecimal valor, BigDecimal quantidade) {

        StringBuilder mensagem = new StringBuilder();
        mensagem.append("EXCHANGE: ");
        mensagem.append(sigla);
        mensagem.append("\tVALOR: ");
        mensagem.append(valor);
        mensagem.append("\tQUANTIDADE: ");
        mensagem.append(quantidade);

        return mensagem.toString();
    }


    /**
     * Calcula os valores de uma Compra e uma Venda
     *
     * @param ticker - Recebe um Ticker com os dados
     */
    private void showCalcularDialog(final Ticker ticker) {

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivityDrawer.this);
        AlertDialog dialog;

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_calcular, null);
        alert.setView(dialogView);

        final EditText inpTotalEmBTCParaCompra = (EditText) dialogView.findViewById(R.id.inpTotalEmBTCParaCompra);
        final EditText inpValorCoinBuy = (EditText) dialogView.findViewById(R.id.inpValorCoinBuy);
        final EditText inpValorCoinSell = (EditText) dialogView.findViewById(R.id.inpValorCoinSell);
        final TextView txtTotalCoinAdquirido = (TextView) dialogView.findViewById(R.id.txtTotalCoinAdquirido);
        final TextView txtTotalBTCAdquirido = (TextView) dialogView.findViewById(R.id.txtTotalBTCAdquirido);
        final TextView txtCalcularROI = (TextView) dialogView.findViewById(R.id.txtCalcularROI);
        final ImageButton imgCalcular = (ImageButton) dialogView.findViewById(R.id.imgCalcular);

        txtCalcularROI.setText("Calcular ROI - " + ticker.getSigla());

        if(ticker.getValorDeCompra().compareTo(BigDecimal.ZERO)==1){
            inpValorCoinBuy.setText(ticker.getValorDeCompra().toString());
        }

        //Setando os hints
        inpValorCoinBuy.setHint(getHintTaxaCompra(ticker));
        inpValorCoinSell.setHint(getHintTaxaVenda(ticker));
        txtTotalCoinAdquirido.setHint(getHintTotalAdiquirido(ticker));

        imgCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (inpTotalEmBTCParaCompra.getText().length() < 1 || new BigDecimal(inpTotalEmBTCParaCompra.getText().toString()).compareTo(new BigDecimal("0.0")) != 1) {
                    Toast.makeText(getApplicationContext(), "Total para Compra inválido", Toast.LENGTH_LONG).show();
                } else if (inpValorCoinBuy.getText().length() < 1 || new BigDecimal(inpValorCoinBuy.getText().toString()).compareTo(new BigDecimal("0.0")) != 1) {
                    Toast.makeText(getApplicationContext(), "Taxa para Compra inválida", Toast.LENGTH_LONG).show();
                } else if (inpValorCoinSell.getText().length() < 1 || new BigDecimal(inpValorCoinSell.getText().toString()).compareTo(new BigDecimal("0.0")) != 1) {
                    Toast.makeText(getApplicationContext(), "Taxa para Venda inválida", Toast.LENGTH_LONG).show();
                } else {

                    //tudo foi validado
                    BigDecimal coinsOptidoPelaCompra = new BigDecimal(calcularCompra(new BigDecimal(inpTotalEmBTCParaCompra.getText().toString()),
                            new BigDecimal(inpValorCoinBuy.getText().toString())));

                    txtTotalCoinAdquirido.setText(coinsOptidoPelaCompra.toString());

                    BigDecimal btcOptidoPelaVenda = calcularVenda(new Double(coinsOptidoPelaCompra.toString()), new BigDecimal(inpValorCoinSell.getText().toString()));

                    txtTotalBTCAdquirido.setText(btcOptidoPelaVenda.toString());

                    if (btcOptidoPelaVenda.compareTo(new BigDecimal(inpTotalEmBTCParaCompra.getText().toString())) == -1) {
                        txtTotalBTCAdquirido.setBackgroundColor(Color.RED);
                    } else {
                        txtTotalBTCAdquirido.setBackgroundColor(Color.GREEN);
                    }

                }
            }
        });

        alert.setNegativeButton("SAIR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                return;
            }
        });

        dialog = alert.create();
        dialog.setIcon(R.drawable.ic_play_circle_outline_black_24dp);
        dialog.show();
    }

    /**
     * Cria a notificação pra moeda selecionada
     */
    private void showDialogCriarNotificacao() {

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivityDrawer.this);
        AlertDialog dialog;

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_notificacao, null);
        alert.setView(dialogView);

        inpValorAvisoBuyAbaixo = dialogView.findViewById(R.id.inpValorAvisoBuyInferior);
        inpValorAvisoBuyAcima = dialogView.findViewById(R.id.inpValorAvisoBuySuperior);
        inpValorAvisoSellAbaixo = dialogView.findViewById(R.id.inpValorAvisoSellInferior);
        inpValorAvisoSellAcima = dialogView.findViewById(R.id.inpValorAvisoSellSuperior);
        inpValorDeCompra = dialogView.findViewById(R.id.inpValorDeCompra);
        TextView txtAvisoDeTaxa = dialogView.findViewById(R.id.txtAvisoDeTaxa);
        tbBuyAbaixo = dialogView.findViewById(R.id.tbBuyInferior);

        txtAvisoDeTaxa.setText("AVISO DE TAXA - " + ticker.getSigla());

        tbBuyAbaixo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tbBuyAbaixo.isChecked()) {
                    tbBuyAbaixo.setBackgroundColor(Color.GREEN);
                } else {
                    tbBuyAbaixo.setBackgroundColor(Color.RED);
                }
            }
        });

        tbBuyAcima = dialogView.findViewById(R.id.tbBuySuperior);
        tbBuyAcima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (tbBuyAcima.isChecked()) {
                    tbBuyAcima.setBackgroundColor(Color.GREEN);
                } else {
                    tbBuyAcima.setBackgroundColor(Color.RED);
                }
            }
        });

        tbSellAbaixo = dialogView.findViewById(R.id.tbSellInferior);
        tbSellAbaixo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tbSellAbaixo.isChecked()) {
                    tbSellAbaixo.setBackgroundColor(Color.GREEN);
                } else {
                    tbSellAbaixo.setBackgroundColor(Color.RED);

                }
            }
        });

        tbSellAcima = (ToggleButton) dialogView.findViewById(R.id.tbSellSuperior);
        tbSellAcima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tbSellAcima.isChecked()) {
                    tbSellAcima.setBackgroundColor(Color.GREEN);
                } else {
                    tbSellAcima.setBackgroundColor(Color.RED);

                }
            }
        });


        //Ja tem aviso salvo Buy Inferior?
        if (ticker.getAvisoBuyInferior().compareTo(new BigDecimal("0.0")) == 1) {
            tbBuyAbaixo.setBackgroundColor(Color.GREEN);
            tbBuyAbaixo.setChecked(true);
            inpValorAvisoBuyAbaixo.setText(ticker.getAvisoBuyInferior().toString());
        } else {
            tbBuyAbaixo.setBackgroundColor(Color.RED);
        }

        //Ja tem aviso salvo Buy Superior?
        if (ticker.getAvisoBuySuperior().compareTo(new BigDecimal("0.0")) == 1) {
            tbBuyAcima.setBackgroundColor(Color.GREEN);
            tbBuyAcima.setChecked(true);
            inpValorAvisoBuyAcima.setText(ticker.getAvisoBuySuperior().toString());
        } else {
            tbBuyAcima.setBackgroundColor(Color.RED);
        }

        //Ja tem aviso salvo Sell Inferior?
        if (ticker.getAvisoStopLoss().compareTo(new BigDecimal("0.0")) == 1) {
            tbSellAbaixo.setBackgroundColor(Color.GREEN);
            tbSellAbaixo.setChecked(true);
            inpValorAvisoSellAbaixo.setText(ticker.getAvisoStopLoss().toString());
        } else {
            tbSellAbaixo.setBackgroundColor(Color.RED);
        }

        //Ja tem aviso salvo Sell Superior?
        if (ticker.getAvisoStopGain().compareTo(new BigDecimal("0.0")) == 1) {
            tbSellAcima.setBackgroundColor(Color.GREEN);
            tbSellAcima.setChecked(true);
            inpValorAvisoSellAcima.setText(ticker.getAvisoStopGain().toString());

        } else {
            tbSellAcima.setBackgroundColor(Color.RED);

        }

        BigDecimal valorDeCompra = ticker.getValorDeCompra();
        if(valorDeCompra.compareTo(BigDecimal.ZERO)==0){
            inpValorDeCompra.setText("");
        }else{
            inpValorDeCompra.setText(valorDeCompra.toString());
        }


        alert.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                boolean flagBuyInferior = false;
                boolean flagBuySuperior = false;
                boolean flagSellInferior = false;
                boolean flagSellSuperior = false;


                if(inpValorDeCompra.getText().length()==0){
                   ticker.setValorDeCompra(BigDecimal.ZERO.setScale(8));
                }else{
                    ticker.setValorDeCompra(new BigDecimal(inpValorDeCompra.getText().toString().trim()));
                }

                //Se está checado então tem que possuir dado
                if (tbBuyAbaixo.isChecked()) {

                    if (inpValorAvisoBuyAbaixo.getText().toString().length() > 0) {
                        ticker.setAvisoBuyInferior(new BigDecimal(inpValorAvisoBuyAbaixo.getText().toString()));
                    } else {
                        Toast.makeText(getApplicationContext(), "Digite um valor para o Aviso de Compra", Toast.LENGTH_LONG).show();
                        flagBuyInferior = true;
                    }
                } else {
                    ticker.setAvisoBuyInferior(new BigDecimal("0.0"));
                }

                if (tbBuyAcima.isChecked()) {

                    if (inpValorAvisoBuyAcima.getText().toString().length() > 0) {
                        ticker.setAvisoBuySuperior(new BigDecimal(inpValorAvisoBuyAcima.getText().toString()));
                    } else {
                        Toast.makeText(getApplicationContext(), "Digite um valor para o Aviso de Compra", Toast.LENGTH_LONG).show();
                        flagBuySuperior = true;
                    }
                } else {
                    ticker.setAvisoBuySuperior(new BigDecimal("0.0"));
                }

                if (tbSellAbaixo.isChecked()) {

                    if (inpValorAvisoSellAbaixo.getText().toString().length() > 0) {
                        ticker.setAvisoStopLoss(new BigDecimal(inpValorAvisoSellAbaixo.getText().toString()));
                    } else {
                        Toast.makeText(getApplicationContext(), "Digite um valor para o Aviso de Venda", Toast.LENGTH_LONG).show();
                        flagSellInferior = true;
                    }

                } else {
                    ticker.setAvisoStopLoss(new BigDecimal("0.0"));
                }

                if (tbSellAcima.isChecked()) {

                    if (inpValorAvisoSellAcima.getText().toString().length() > 0) {
                        ticker.setAvisoStopGain(new BigDecimal(inpValorAvisoSellAcima.getText().toString()));
                    } else {
                        Toast.makeText(getApplicationContext(), "Digite um valor para o Aviso de Venda", Toast.LENGTH_LONG).show();
                        flagSellSuperior = true;
                    }

                } else {
                    ticker.setAvisoStopGain(new BigDecimal("0.0"));
                }


                if (flagBuyInferior && flagBuySuperior && flagSellInferior && flagSellSuperior) {

                    Toast.makeText(getApplicationContext(), "Digite um valor", Toast.LENGTH_LONG).show();
                }

                long retorno = tickerDAO.update(ticker);

                if (retorno == 0)
                    Toast.makeText(MainActivityDrawer.this, "Não foi possível executar a operação", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivityDrawer.this, "Notificação atualizada com sucesso", Toast.LENGTH_SHORT).show();

            }
        })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        return;
                    }
                });


        dialog = alert.create();
        dialog.setIcon(R.drawable.ic_add_alert_black_24dp);
        dialog.show();

    }

    /**
     * @param ticker -
     * @return - Uma String com o texto para o Hint pra Compra
     */
    private String getHintTaxaCompra(Ticker ticker) {
        //Colocando os hint
        StringBuilder hint = new StringBuilder();
        hint.append("Taxa ");
        hint.append(ticker.getSigla());
        hint.append(" para Compra");
        return hint.toString();
    }

    /**
     * @param ticker -
     * @return - Uma String com o texto para o Hint pra Venda
     */
    private String getHintTaxaVenda(Ticker ticker) {
        //Colocando os hint
        StringBuilder hint = new StringBuilder();
        hint.append("Taxa ");
        hint.append(ticker.getSigla());
        hint.append(" para Venda");
        return hint.toString();
    }

    /**
     * @param ticker -
     * @return - Uma String com o texto para o Hint Total Adiquirido
     */
    private String getHintTotalAdiquirido(Ticker ticker) {
        //Colocando os hint
        StringBuilder hint = new StringBuilder();
        hint.append("Total de ");
        hint.append(ticker.getSigla());
        hint.append(" adquirido");
        return hint.toString();
    }

    /**
     * @param totalEmBTC Valor em que se deseja aplicar
     * @return uma String com a quantidade de Altcoin
     */
    private String calcularCompra(BigDecimal totalEmBTC, BigDecimal taxaBuy) {

        //formato de retorno
        DecimalFormat df = new DecimalFormat("#.########");

        if (!(totalEmBTC.compareTo(new BigDecimal(0.0)) == 1)) {
            return "0.00000000";
        }

        BigDecimal buyTemp = taxaBuy;
        //Converte para Double para efetuar o calculo
        Double doubleTaxaBuy = new Double(buyTemp.toString());

        //Converte para Dougle para efetuar o calculo
        Double doubleTotalEmBTC = new Double(totalEmBTC.toString());

        Double totalEmAltCoin;

        totalEmAltCoin = doubleTotalEmBTC / doubleTaxaBuy;
        totalEmAltCoin -= totalEmAltCoin * TAXA_BUY;

        String retorno = df.format(totalEmAltCoin).replace(",", ".");

        return retorno;

    }

    /**
     * Simula um venda de AltCoin
     *
     * @param
     * @return - Um BigDecimal com o valor da venda
     */
    private BigDecimal calcularVenda(Double valorEmAltCoin, BigDecimal taxaSell) {

        //formato de retorno
        DecimalFormat df = new DecimalFormat("#.########");

        BigDecimal sellTemp = taxaSell;

        //converte em Double para efetuar o Calculo
        Double doubleTaxaSell = new Double(sellTemp.toString());

        BigDecimal totalAltCoins;
        Double doubleTotalSellAltCoins;

        doubleTotalSellAltCoins = valorEmAltCoin * doubleTaxaSell;
        doubleTotalSellAltCoins -= doubleTotalSellAltCoins * TAXA_SELL;

        String retorno = df.format(new Double(doubleTotalSellAltCoins.toString()));

        totalAltCoins = new BigDecimal(retorno.replace(",", "."));

        return totalAltCoins;

    }


    private synchronized void atualizarListView(LinkedList<Ticker> tickers) {

        if (adapterExchanges == null) {
            adapterExchanges = new AdapterExchanges(MainActivityDrawer.this, tickers);
            lstTicker.setAdapter(adapterExchanges);

        } else {
            adapterExchanges.clear();
            adapterExchanges.addAll(tickers);
            adapterExchanges.notifyDataSetChanged();
        }


    }


    /**
     * Adiciona uma nova moeda
     */
    private void showDialogAdicionarMoeda() {

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivityDrawer.this);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_cadastrar_coin, null);
        alert.setView(dialogView);

        final EditText inpSiglaMoeda = dialogView.findViewById(R.id.inpSiglaMoeda);
        TextView txtAdicionar = dialogView.findViewById(R.id.txtAdicionar);


        txtAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //verifica se a sigla não esta vazia
                if (inpSiglaMoeda.getText().length() < 1) {
                    Toast.makeText(MainActivityDrawer.this, "Campo Sigla esta vazio.", Toast.LENGTH_LONG).show();
                    return;
                }

                Ticker t = new Ticker();
                t.setSigla(inpSiglaMoeda.getText().toString());
                String nomeExchange = mapSiglaNomeExchange.get(t.getSigla());


                if (nomeExchange == null) {
                    Toast.makeText(MainActivityDrawer.this, "Sigla inválida", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    if (nomeExchange.toUpperCase().equals("BTC")) {
                        Toast.makeText(MainActivityDrawer.this, "Sigla inválida", Toast.LENGTH_LONG).show();
                        return;
                    }
                    t.setNomeExchange(nomeExchange);
                    t.setUrlApi(WebServiceUtil.getUrl() + t.getSigla().toLowerCase());
                }

                try {
                    dialog = new ProgressDialog(MainActivityDrawer.this);
                    dialog.setMessage("Processando...");
                    dialog.setTitle("BittrexAnalizer");
                    dialog.show();

                    //Salva no BD
                    t.setId(tickerDAO.create(t));

                    //Se salvou, atualiza
                    if (t.getId() != null) {
                        Toast.makeText(MainActivityDrawer.this, "Salvo com Sucesso", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivityDrawer.this, "Não foi possível executar a operação", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    Log.i(LOG, e.getMessage());
                } finally {
                    dialog.dismiss();
                    alertDialog.cancel();
                    getTicker();
                }
            }
        });


        alert.setNegativeButton("SAIR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                return;
            }
        });

        alertDialog = alert.create();
        alertDialog.setIcon(R.drawable.ic_add_black_24dp);
        alertDialog.show();

    }

    /**
     * Deleta a moeda Selecionada
     */
    private void showDialogDeletarMoeda() {

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivityDrawer.this);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_deletar_coin, null);
        alert.setView(dialogView);

        final EditText inpSiglaMoeda = dialogView.findViewById(R.id.inpSiglaMoeda);
        final EditText inpNomeMoeda = dialogView.findViewById(R.id.inpNomeMoeda);
        final TextView txtDeletar = dialogView.findViewById(R.id.txtDeletar);

        inpSiglaMoeda.setText(ticker.getSigla());
        inpNomeMoeda.setText(ticker.getNomeExchange());

        txtDeletar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {
                    dialog = new ProgressDialog(MainActivityDrawer.this);
                    dialog.setMessage("Processando...");
                    dialog.setTitle("BittrexAnalizer");
                    dialog.show();

                    //Deleta do BD
                    tickerDAO.delete(ticker);

                    Toast.makeText(MainActivityDrawer.this, "Deletado com sucesso", Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    Log.i(LOG, e.getMessage());
                } finally {
                    dialog.dismiss();
                    alertDialog.cancel();
                    getTicker();
                }

            }
        });

        alert.setNegativeButton("SAIR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                return;
            }
        });

        alertDialog = alert.create();
        alertDialog.setIcon(R.drawable.ic_delete_black_24dp);
        alertDialog.show();

    }

    private void showDialogDesligarNotificacoes() {

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivityDrawer.this);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_confirmar_desligar_notificacoes, null);
        alert.setView(dialogView);

        final TextView txtDeligarNotificacao = dialogView.findViewById(R.id.txtDeligarNotificacao);

        txtDeligarNotificacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stopServiceTaxa();
                Toast.makeText(MainActivityDrawer.this, "Desligado o Serviço de Notificações", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setNegativeButton("SAIR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                return;
            }
        });

        alertDialog = alert.create();
        alertDialog.setIcon(R.drawable.ic_alarm_off_black_24dp);
        alertDialog.show();

    }

    @Override
    public void run() {

        try {
            mapSiglaNomeExchange = HttpClient.findAllCurrencies();
            SessionUtil.getInstance().setNomeExchanges(mapSiglaNomeExchange);
        } catch (Exception e) {
            Log.i(LOG, e.getMessage());
        }
    }


    /**
     * Método que atualiza a lista de 30 em 30 segundos
     */
    private class MyTask extends TimerTask {

        @Override
        public void run() {
            try {

                getTicker();

                if(SessionUtil.getInstance().getMapBalances().containsKey("BTC")) {
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            if (new Boolean(SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.ROBOT_LIGADO))) {

                                BalanceStrategy.execute();

                                //Se não tiver valor será stopado o Service Compra
                                Balance b = SessionUtil.getInstance().getMapBalances().get("BTC");
                                BigDecimal valorCompraRobot = new BigDecimal(SessionUtil.getInstance().getMapConfiguracao()
                                        .get(ConstantesUtil.VALOR_COMPRA_ROBOT)).setScale(8);
                                if (b.getBalance().compareTo(valorCompraRobot) == -1) {
                                    stopServiceCompra();
                                } else {
                                    startServiceCompra();
                                }
                            } else {
                                stopServiceCompra();
                            }
                        }
                    });
                    t.start();
                }

            } catch (Exception e) {
                Log.i(LOG, e.getMessage());
            }
        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_help) {

        } else if (id == R.id.action_setttings) {
            Intent i = new Intent(this, UserConfiguracao.class);
            startActivity(i);
        } else if (id == R.id.action_execute) {


            AlarmAnalizerCompraStrategy alarmAnalizerCompraStrategy = new AlarmAnalizerCompraStrategy();
            alarmAnalizerCompraStrategy.executar(MainActivityDrawer.this);

        } else if (id == R.id.action_calcular_porcentagem) {

            showCalcularPorcentagemDialog();

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        final String NOME_ASC = "NOME ASC";
        final String NOME_DESC = "NOME DESC";
        final String LAST_ASC = "LAST ASC";
        final String LAST_DESC = "LAST DESC";

        if (id == R.id.nav_add_coin) {
            showDialogAdicionarMoeda();
        } else if (id == R.id.nav_notification) {
            showDialogDesligarNotificacoes();
        } else if (id == R.id.nav_clas_nome_moeda) {

            if (tickers == null || tickers.size() == 0) {
                Toast.makeText(MainActivityDrawer.this, "A Lista esta vazia", Toast.LENGTH_LONG).show();
                return true;
            }

            if (contadorNomeClassificacao % 2 == 0)
                Collections.sort(tickers);
            else
                Collections.reverse(tickers);

            contadorNomeClassificacao++;

        } else if (id == R.id.nav_clas_last) {

            if (tickers == null || tickers.size() == 0) {
                Toast.makeText(MainActivityDrawer.this, "A Lista esta vazia", Toast.LENGTH_LONG).show();
                return true;
            }


            if (contadorLastClassificacao % 2 == 0)
                tickers = ordenar(LAST_ASC, tickers);
            else
                tickers = ordenar(LAST_DESC, tickers);

            contadorLastClassificacao++;

        } else if (id == R.id.nav_open_orders) {
            Intent i = new Intent(this, UserOpenOrders.class);
            startActivity(i);

        } else if (id == R.id.nav_orders) {
            Intent i = new Intent(this, UserOrders.class);
            startActivity(i);

        } else if (id == R.id.nav_balances) {
            Intent i = new Intent(this, UserBalances.class);
            startActivity(i);
        } else if (id == R.id.nav_keys) {
            Intent i = new Intent(this, UserKey.class);
            startActivity(i);
        } else if (id == R.id.nav_start_service_compra) {
            startServiceCompra();
        } else if (id == R.id.nav_stop_service_compra) {
            stopServiceCompra();
        } else if (id == R.id.nav_start_service_venda) {
            startServiceVenda();
        } else if (id == R.id.nav_stop_service_venda) {
            stopServiceVenda();
        }

        verificarMenuService();

        atualizarListView(tickers);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showCalcularPorcentagemDialog() {

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivityDrawer.this);
        AlertDialog dialog;

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_calcular_porcentagem, null);
        alert.setView(dialogView);

        final EditText inpValorAplicado = dialogView.findViewById(R.id.inpValorAplicado);
        final EditText inpPorcentagem = dialogView.findViewById(R.id.inpPorcentagem);
        final TextView txtResutado = dialogView.findViewById(R.id.txtResultado);
        final ImageButton imgCalcular = dialogView.findViewById(R.id.imgCalcular);


        //Setando os hints

        imgCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                BigDecimal porcentagem = new BigDecimal(inpPorcentagem.getText().
                        toString()).setScale(2);
                BigDecimal valorAplicado = new BigDecimal(inpValorAplicado.getText().toString());

                txtResutado.setText(CalculoUtil.getPorcentagem(valorAplicado, porcentagem).toString());

            }
        });

        alert.setNegativeButton("SAIR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                return;
            }
        });

        dialog = alert.create();
        dialog.setIcon(R.drawable.ic_play_circle_outline_black_24dp);
        dialog.show();
    }

}
