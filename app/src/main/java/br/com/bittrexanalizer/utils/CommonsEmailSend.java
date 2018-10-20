package br.com.bittrexanalizer.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by PauLinHo on 28/09/2017.
 */

public class CommonsEmailSend {

    private Session session;
    private Context context;
    private String PASSWORD = "bittrexanalizer07";
    private String rec;
    private String subject;
    private String textMessage;
    private final String EMAIL_SERVIDOR = "bittrexanalizer@omniatechnology.com.br";


    public CommonsEmailSend(Context context, String rec, String subject, String textMessage) {
        this.context = context;
        this.rec = rec;
        this.subject = subject;
        this.textMessage = textMessage;
    }

    public void sendMail() {

        Properties props = new Properties();
        props.put("mail.smtp.host", "mail.omniatechnology.com.br");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_SERVIDOR, PASSWORD);
            }
        });


        try {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {

                    Message message = new MimeMessage(session);
                    try {
                        message.setFrom(new InternetAddress(EMAIL_SERVIDOR));

                        message.setRecipients(RecipientType.TO, InternetAddress.parse(rec));
                        message.setSubject(subject);
                        message.setContent(textMessage, "text/html; charset=utf-8");

                        Transport.send(message);

                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }

                }
            });
            t.start();
        } catch (Exception e) {


        }


        class RetrieveFeedTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... strings) {
                try {

                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(EMAIL_SERVIDOR));
                    message.setRecipients(RecipientType.TO, InternetAddress.parse(rec));
                    message.setSubject(subject);
                    message.setContent(textMessage, "text/html; charset=utf-8");

                    Transport.send(message);


                } catch (MessagingException m) {

                } catch (Exception e) {

                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {

                Toast.makeText(context, "Mensagem enviada com sucesso!", Toast.LENGTH_LONG).show();
            }

        }
    }
}

