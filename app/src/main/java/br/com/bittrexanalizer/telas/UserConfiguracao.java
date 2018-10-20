package br.com.bittrexanalizer.telas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import br.com.bittrexanalizer.R;
import br.com.bittrexanalizer.adapters.AdapterConfiguracoes;
import br.com.bittrexanalizer.database.dao.ConfiguracaoDAO;
import br.com.bittrexanalizer.domain.Configuracao;
import br.com.bittrexanalizer.utils.ConfiguracaoComparator;
import br.com.bittrexanalizer.utils.SessionUtil;

/**
 * Created by PauLinHo on 16/01/2018.
 */

public class UserConfiguracao extends Activity implements IFlagment {


    private Configuracao configuracao;
    private ListView lstConfiguracoes;
    private AdapterConfiguracoes adapterConfiguracoes;
    private LinkedList<Configuracao> configuracoes;

    private ImageButton imgButtonNewCrud;
    private ImageButton imgButtonDeleteCrud;
    private ImageButton imgButtonEditCrud;

    private boolean foiPersistido = false;
    private String operacaoEscolhida = "";

    private LayoutInflater inflater;
    private AlertDialog alert;
    private AlertDialog dialog;
    private AlertDialog.Builder dialogBuilder;

    private ConfiguracaoDAO configuracaoDAO;

    private Handler handler = new Handler();
    private EditText inpConfiguracaoValor;
    private EditText inpConfiguracaoPropriedade;

    //variables bundle
    private boolean flagIsStateSaved = false;
    private String configuracaoValorSaved;
    private String configuracaoPropriedadeSaved;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_configuration);

        if (savedInstanceState != null) {
            flagIsStateSaved = true;
            configuracaoValorSaved = savedInstanceState.getString("configuracaoValorSaved");
            configuracaoPropriedadeSaved = savedInstanceState.getString("configuracaoPropriedadeSaved");
        }

        lstConfiguracoes = findViewById(R.id.lstConfiguracoes);
        configuracao = new Configuracao();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabConfiguracao);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                configuracao = new Configuracao();
                criarDialogCRUD(IFlagment.CADASTRAR);
            }
        });


        configuracaoDAO = new ConfiguracaoDAO(UserConfiguracao.this);

        atualizarListView();

        lstConfiguracoes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                configuracao = new Configuracao();
                configuracao = (Configuracao) lstConfiguracoes.getItemAtPosition(i);

                showDialog();

                return true;
            }
        });

        Toast.makeText(this, "CONFIGURAÇÕES NECESSÁRIA PARA O SISTEMA FUNCIONAR CORRETAMENTE", Toast.LENGTH_SHORT).show();

    }

    /**
     * Exibe o dialog para escolher a opçao de CRUD
     * Variavel que indica se foi clicado de do fat(TRUE) ou do ListView(false)
     * Se foi clicado do Flag nao ativa a opçao de exclusao e delete
     */
    private void showDialog() {

        final AlertDialog.Builder dialogCrud = new AlertDialog.Builder(UserConfiguracao.this);

        inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_crud, null);
        dialogCrud.setView(dialogView);
        dialogCrud.setTitle(IFlagment.MENSAGEM_DIALOG);

        final View.OnClickListener criarDialog = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imgButtonNewCrud.getId() == view.getId()) {
                    configuracao = new Configuracao();
                    criarDialogCRUD(IFlagment.CADASTRAR);
                } else if (imgButtonDeleteCrud.getId() == view.getId()) {
                    criarDialogCRUD(IFlagment.DELETAR);
                } else {
                    criarDialogCRUD(IFlagment.EDITAR);
                }

                alert.dismiss();
            }

        };

        imgButtonNewCrud = (ImageButton) dialogView.findViewById(R.id.imgButtonNewCrud);
        imgButtonDeleteCrud = (ImageButton) dialogView.findViewById(R.id.imgButtonDeleteCrud);
        imgButtonEditCrud = (ImageButton) dialogView.findViewById(R.id.imgButtonEditCrud);

        imgButtonNewCrud.setOnClickListener(criarDialog);
        imgButtonDeleteCrud.setOnClickListener(criarDialog);
        imgButtonEditCrud.setOnClickListener(criarDialog);

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
     * Cria o dialog para realizar a operação solicitada
     *
     * @param operacao Operação clicada {NOVO, EDITAR, DELETAR}
     */
    private void criarDialogCRUD(final String operacao) {

        operacaoEscolhida = operacao;

        dialogBuilder = new AlertDialog.Builder(UserConfiguracao.this);

        inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_configuracao, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("CONFIGURAÇÃO" + " - " + operacao.toUpperCase());

        inpConfiguracaoPropriedade = (EditText) dialogView.findViewById(R.id.inpDialogConfiguracaoPropriedade);
        inpConfiguracaoValor = (EditText) dialogView.findViewById(R.id.inpDialogConfiguracaoValor);

        if (flagIsStateSaved) {
            verificarStateSalvo();
        }


        //Se não for CADASTRAR preenche o Dialog
        if (!operacao.equals(IFlagment.CADASTRAR)) {
            inpConfiguracaoPropriedade.setText(configuracao.getPropriedade());
            inpConfiguracaoValor.setText(configuracao.getValor());
        }

        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                configuracao.setPropriedade(inpConfiguracaoPropriedade.getText().toString().toUpperCase());
                configuracao.setValor(inpConfiguracaoValor.getText().toString());

                //Foi validado?
                String mensagem = validar(configuracao);
                if (!(mensagem.length() == 0)) {
                    Toast.makeText(UserConfiguracao.this, mensagem, Toast.LENGTH_SHORT).show();
                    return;
                }

                executar();

            }
        })


                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        return;

                    }
                });

        dialog = dialogBuilder.create();
        dialog.show();

    }

    private void verificarStateSalvo() {
        inpConfiguracaoPropriedade.setText(configuracaoPropriedadeSaved);
        inpConfiguracaoValor.setText(configuracaoValorSaved);
    }

    /**
     * Valida a entidade recebida
     *
     * @return returno uma string vazia se foi validada
     * ou uma string com a mensagem do erro
     */
    private String validar(Configuracao configuracao) {

        String retorno = "";

        if (configuracao.getPropriedade().length() < 1) {
            return retorno = "O campo Propriedade esta vazio.";
        } else if (configuracao.getValor().length() < 1) {
            return retorno = "O campo Valor esta vazio.";
        }

        return retorno;
    }

    /**
     * Execute o CRUD
     */
    private void executar() {

        foiPersistido = false;
        if (operacaoEscolhida.equals(IFlagment.DELETAR))
            configuracaoDAO.delete(configuracao);
        else if (operacaoEscolhida.equals(IFlagment.EDITAR))
            configuracaoDAO.update(configuracao);
        else
            configuracaoDAO.create(configuracao);

        atualizarListView();

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (inpConfiguracaoValor != null && inpConfiguracaoPropriedade != null) {
            outState.putString("configuracaoPropriedadeSaved", inpConfiguracaoPropriedade.getText().toString());
            outState.putString("configuracaoValorSaved", inpConfiguracaoValor.getText().toString());
        }

    }


    public void atualizarListView() {

        configuracoes = configuracaoDAO.all();

        configuracoes = ConfiguracaoComparator.ordenar(ConfiguracaoComparator.PROP_ASC, configuracoes);

        if (adapterConfiguracoes == null) {
            adapterConfiguracoes = new AdapterConfiguracoes(UserConfiguracao.this, configuracoes);
            lstConfiguracoes.setAdapter(adapterConfiguracoes);
        } else {
            adapterConfiguracoes.clear();
            adapterConfiguracoes.notifyDataSetChanged();
            adapterConfiguracoes.addAll(configuracoes);
        }

    }


    public void atualizarSessionUtil(){

            LinkedList<Configuracao> configuracoes = new LinkedList<>();
            configuracoes = new ConfiguracaoDAO(UserConfiguracao.this).all();

            Map<String, String> mapConfiguracao = new HashMap<String, String>();

            if(configuracoes==null){
                SessionUtil.getInstance().setMapConfiguracao(null);
                return;
            }

            for(Configuracao c : configuracoes){
                mapConfiguracao.put(c.getPropriedade(), c.getValor());
            }

            SessionUtil.getInstance().setMapConfiguracao(mapConfiguracao);



    }


}
