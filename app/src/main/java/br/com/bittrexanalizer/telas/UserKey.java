package br.com.bittrexanalizer.telas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.LinkedList;

import br.com.bittrexanalizer.R;
import br.com.bittrexanalizer.adapters.AdapterKeys;
import br.com.bittrexanalizer.api.ApiCredentials;
import br.com.bittrexanalizer.database.dao.ApiCredentialsDAO;

/**
 * Created by PauLinHo on 16/01/2018.
 */

public class UserKey extends Activity implements IFlagment {


    private ApiCredentials apiCredentials;
    private ListView lstApiCredentials;
    private AdapterKeys adapterApiCredentials;
    private LinkedList<ApiCredentials> listaApiCredentials;

    private ImageButton imgButtonNewCrud;
    private ImageButton imgButtonDeleteCrud;
    private ImageButton imgButtonEditCrud;

    private boolean foiPersistido = false;
    private String operacaoEscolhida = "";

    private LayoutInflater inflater;
    private AlertDialog alert;
    private AlertDialog dialog;
    private AlertDialog.Builder dialogBuilder;

    private ApiCredentialsDAO apiCredentialsDAO;

    private EditText inpApiCredentialsKey;
    private EditText inpApiCredentialsSecret;

    //variables bundle
    private boolean flagIsStateSaved = false;
    private String apiCredentialsSecretSaved;
    private String apiCredentialsKeySaved;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_keys);

        if (savedInstanceState != null) {
            flagIsStateSaved = true;
            apiCredentialsSecretSaved = savedInstanceState.getString("apiCredentialsSecretSaved");
            apiCredentialsKeySaved = savedInstanceState.getString("apiCredentialsKeySaved");
        }

        lstApiCredentials = findViewById(R.id.lstApiCredentials);
        apiCredentials = new ApiCredentials();

        FloatingActionButton fab = findViewById(R.id.fabKey);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apiCredentials = new ApiCredentials();
                criarDialogCRUD(IFlagment.CADASTRAR);
            }
        });


        apiCredentialsDAO = new ApiCredentialsDAO(UserKey.this);

        atualizarListView();

        lstApiCredentials.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                apiCredentials = new ApiCredentials();
                apiCredentials = (ApiCredentials) lstApiCredentials.getItemAtPosition(i);

                showDialog();

                return true;
            }
        });

    }

    /**
     * Exibe o dialog para escolher a opçao de CRUD
     * Variavel que indica se foi clicado de do fat(TRUE) ou do ListView(false)
     * Se foi clicado do Flag nao ativa a opçao de exclusao e delete
     */
    private void showDialog() {

        final AlertDialog.Builder dialogCrud = new AlertDialog.Builder(UserKey.this);

        inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_crud, null);
        dialogCrud.setView(dialogView);
        dialogCrud.setTitle(IFlagment.MENSAGEM_DIALOG);

        final View.OnClickListener criarDialog = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imgButtonNewCrud.getId() == view.getId()) {
                    apiCredentials = new ApiCredentials();
                    criarDialogCRUD(IFlagment.CADASTRAR);
                } else if (imgButtonDeleteCrud.getId() == view.getId()) {
                    criarDialogCRUD(IFlagment.DELETAR);
                } else {
                    criarDialogCRUD(IFlagment.EDITAR);
                }

                alert.dismiss();
            }

        };

        imgButtonNewCrud = dialogView.findViewById(R.id.imgButtonNewCrud);
        imgButtonDeleteCrud =  dialogView.findViewById(R.id.imgButtonDeleteCrud);
        imgButtonEditCrud = dialogView.findViewById(R.id.imgButtonEditCrud);

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

        dialogBuilder = new AlertDialog.Builder(UserKey.this);

        inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_key, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("API CREDENTIALS" + " - " + operacao.toUpperCase());

        inpApiCredentialsKey = dialogView.findViewById(R.id.inpDialogApiCredentialsKey);
        inpApiCredentialsSecret = dialogView.findViewById(R.id.inpDialogApiCredentialsSecret);

        if (flagIsStateSaved) {
            verificarStateSalvo();
        }


        //Se não for CADASTRAR preenche o Dialog
        if (!operacao.equals(IFlagment.CADASTRAR)) try {
            inpApiCredentialsKey.setText(apiCredentials.getKey());
            inpApiCredentialsSecret.setText(apiCredentials.getSecret());
        } catch (Exception e) {
            e.printStackTrace();
        }

        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                apiCredentials.setKey(inpApiCredentialsKey.getText().toString());
                apiCredentials.setSecret(inpApiCredentialsSecret.getText().toString());

                //Foi validado?
                String mensagem = validar(apiCredentials);
                if (!(mensagem.length() == 0)) {
                    Toast.makeText(UserKey.this, mensagem, Toast.LENGTH_SHORT).show();
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
        inpApiCredentialsKey.setText(apiCredentialsKeySaved);
        inpApiCredentialsSecret.setText(apiCredentialsSecretSaved);
    }

    /**
     * Valida a entidade recebida
     *
     * @return returno uma string vazia se foi validada
     * ou uma string com a mensagem do erro
     */
    private String validar(ApiCredentials apiCredentials) {

        String retorno = "";

        if (apiCredentials.getKey().length() < 1) {
            retorno = "O campo KEY esta vazio.";
        } else if (apiCredentials.getSecret().length() < 1) {
            retorno = "O campo SECRET esta vazio.";
        }

        return retorno;
    }

    /**
     * Execute o CRUD
     */
    private void executar() {

        foiPersistido = false;
        if (operacaoEscolhida.equals(IFlagment.DELETAR))
            apiCredentialsDAO.delete(apiCredentials);
        else if (operacaoEscolhida.equals(IFlagment.EDITAR))
            apiCredentialsDAO.update(apiCredentials);
        else
            apiCredentialsDAO.create(apiCredentials);

        atualizarListView();

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (inpApiCredentialsKey != null && inpApiCredentialsKey != null) {
            outState.putString("apiCredentialsKeySaved", inpApiCredentialsKey.getText().toString());
            outState.putString("apiCredentialsSecretSaved", inpApiCredentialsSecret.getText().toString());
        }

    }


    public void atualizarListView() {

        listaApiCredentials = apiCredentialsDAO.all();
        if (adapterApiCredentials == null) {
            adapterApiCredentials = new AdapterKeys(UserKey.this, listaApiCredentials);
            lstApiCredentials.setAdapter(adapterApiCredentials);
        } else {
            adapterApiCredentials.clear();
            adapterApiCredentials.notifyDataSetChanged();
            adapterApiCredentials.addAll(listaApiCredentials);
        }

    }


}
