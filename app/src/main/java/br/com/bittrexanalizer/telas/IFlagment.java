package br.com.bittrexanalizer.telas;

/**
 * Created by PauLinHo on 25/12/2017.
 */

interface IFlagment {

    String CADASTRAR = "Cadastrar";
    String EDITAR = "Editar";
    String DELETAR = "Deletar";

    long TEMPO_SLEEP = 700;

    String MENSAGEM_DIALOG = "Escolha a Opção";

    void atualizarListView();

}
