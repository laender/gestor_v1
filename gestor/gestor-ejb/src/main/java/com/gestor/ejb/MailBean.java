/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestor.ejb;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author laender
 */
public abstract class MailBean {

    public static void enviar(String assunto, String texto, String attach) {

        Properties props = new Properties();
        MimeBodyPart mbp = new MimeBodyPart();
        /**
         * Parâmetros de conexão com servidor Gmail
         */
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("laender21@gmail.com", "cbr1000rrr");
            }
        });

        /**
         * Ativa Debug para sessão
         */
        session.setDebug(true);

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("laender21@gmail.com")); //Remetente

            Address[] toUser = InternetAddress //Destinatário(s)
                    .parse("laender_santos@hotmail.com");

            message.setRecipients(Message.RecipientType.TO, toUser);
            message.setSubject(assunto);//Assunto
            message.setText(texto);
            if (attach != null) {
                DataSource fds = new FileDataSource(attach);
                mbp.setDisposition(Part.ATTACHMENT);
                mbp.setDataHandler(new DataHandler(fds));
                mbp.setFileName(fds.getName());
                Multipart mp = new MimeMultipart();
                mp.addBodyPart(mbp);
            }

            /**
             * Método para enviar a mensagem criada
             */
            Transport.send(message);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
