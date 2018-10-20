package br.com.bittrexanalizer.utils;

import android.content.Context;

/**
 * Created by PauLinHo on 24/01/2018.
 */

public class SMSUtil {

    private Context context;
    private String operacao;

//    public void enviarSMS(Paciente paciente) {
//
//        String texto = gerarTextSMS(paciente);
//
//        String numero = paciente.getTelefones().get(0).getNumero().replace("(","").replace(")","");
//
//
//
//    }

//    public boolean enviarSMS(Context context, String mensagem, String operacao) {
//
//        this.context = context;
//
//        this.operacao = operacao;
//
//        String assunto = "Aviso de Moeda dentro dos limites técnicos para " + operacao;
//
//        String texto = gerarTextoEmail(mensagem);
//
//        String email = "";
//
//        if (!SessionUtil.getInstance().getMapConfiguracao().containsKey(ConstantesUtil.EMAIL)) {
//            Toast.makeText(context, "Não foi localizado a propriedade EMAIL em configurações", Toast.LENGTH_LONG).show();
//            return false;
//        } else {
//            email = SessionUtil.getInstance().getMapConfiguracao().get(ConstantesUtil.EMAIL).toLowerCase();
//        }
//
//
//        try {
//            ActivityCompat.requestPermissions(MainActivityDrawer.class, new String[]{Manifest.permission.SEND_SMS}, 1);
//            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage(numero, null, texto, null, null);
//
//        } catch (Exception e) {
//            Log.i("REGULAMOGI", e.getMessage());
//        }
//        return true;
//    }
//
//    private String gerarTextoEmail(String mensagem) {
//
//        StringBuilder texto = new StringBuilder();
//        texto.append("BITTREX ANALIZER ");
//        texto.append("\n\n");
//        texto.append(mensagem);
//
//        return texto.toString();
//
//    }

}
