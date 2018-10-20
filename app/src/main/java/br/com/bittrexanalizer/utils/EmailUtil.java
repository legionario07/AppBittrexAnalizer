package br.com.bittrexanalizer.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by PauLinHo on 21/01/2018.
 */

public class EmailUtil {

    private String operacao = "";
    private Context context;
    private String assunto = "";

    public boolean enviarEmail(final Context context, String mensagem, String operacao) {

        this.context = context;

        this.operacao = operacao;

        if(assunto.length() ==0) {
            assunto = "BITRREX " + operacao;
        }

        final String texto = gerarTextoEmail(mensagem);

        String email = "";

        if (!SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.EMAIL)) {
            Toast.makeText(context, "Não foi localizado a propriedade EMAIL em configurações", Toast.LENGTH_LONG).show();
            return false;
        } else {
            email = SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.EMAIL).toLowerCase();
        }


        final String finalEmail = email;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                CommonsEmailSend commonsEmailSend = new CommonsEmailSend(context,
                        finalEmail,
                        assunto,
                        texto);
                commonsEmailSend.sendMail();
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        try {

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public boolean enviarEmail(Context context, String assunto, String mensagem, String operacao) {

        this.assunto = assunto;

        return enviarEmail(context, mensagem, operacao);
    }

    private String gerarTextoEmail(String mensagem) {

        StringBuilder texto = new StringBuilder();
        texto.append("BITTREX ANALIZER ");
        texto.append("\n\n");
        texto.append(mensagem);

        return texto.toString();

    }
}
