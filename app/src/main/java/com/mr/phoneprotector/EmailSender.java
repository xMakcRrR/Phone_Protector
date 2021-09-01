package com.mr.phoneprotector;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

public class EmailSender extends javax.mail.Authenticator {
    private String mailhost = "smtp.gmail.com";
    private String user;
    private String password;
    private Session session;

    public boolean isEthernetReady(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public EmailSender(String l, String p) {
        this.user = l;
        this.password = p;

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", mailhost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        session = Session.getDefaultInstance(props, this);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    public synchronized void sendMail(String subject, String body, String sender, String recipients)
            throws Exception {
        try{
            MimeMessage message = new MimeMessage(session);
            DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
            message.setFrom(new InternetAddress(sender));
            message.setSubject(subject);
            message.setDataHandler(handler);
            if (recipients.indexOf(',') > 0)
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
            else
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
            Transport.send(message);
        }catch(Exception e){

        }
    }

    public void sendMailWithAttachment(String subject, String sender,
                                       String recipient, String coords,
                                       String soundFilepath) throws Exception {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(sender));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
        message.setSubject(subject);

        Multipart multipart = new MimeMultipart();

        //Adding coordinates
        BodyPart coordsBodyPart = new MimeBodyPart();
        if (!coords.equals("")) {
            coordsBodyPart.setContent(coords, "text/plain");
        } else {
            coordsBodyPart.setContent("Amogus hijack", "text/plain");
        }
        multipart.addBodyPart(coordsBodyPart);

        //Adding sound record
        if (!soundFilepath.equals("")) {
            MimeBodyPart soundBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(soundFilepath);
            soundBodyPart.setDataHandler(new DataHandler(source));
            soundBodyPart.setFileName(soundFilepath);
            multipart.addBodyPart(soundBodyPart);
        }

        //Adding photos

        message.setContent(multipart);
        Log.d("Amogus", "Mail away");
        Transport.send(message);
    }
}
