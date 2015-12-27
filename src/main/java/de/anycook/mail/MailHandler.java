/*
 * This file is part of anycook. The new internet cookbook
 * Copyright (C) 2014 Jan Graßegger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see [http://www.gnu.org/licenses/].
 */

/**
 *
 */
package de.anycook.mail;

import com.google.common.base.Preconditions;
import de.anycook.conf.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * Verschickt Mails
 *
 * @author Jan Grassegger
 */
public class MailHandler {

    private Logger logger;
    private Session session;
    private Executor executor;

    private static MailHandler singleton = null;

    public static MailHandler getSingleton() {
        if (singleton == null)
            singleton = new MailHandler();
        return singleton;
    }

    /**
     * Konstruktor. Erstellt allgemeine Properties zum versenden
     */
    private MailHandler() {

        logger = LogManager.getLogger(getClass());
        executor = Executors.newFixedThreadPool(10);


        Properties props = new Properties();
        props.put("mail.smtps.auth", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.host", Configuration.getInstance().getSMTPHost());
        props.put("mail.smtp.socketFactory.port", Configuration.getInstance().getSMTPPort());


        Authenticator auth = new SMTPAuthenticator();
        session = Session.getInstance(props, auth);
        session.setDebug(false);
    }

    public void sendMail(String mailTo, String subject, String message) {
        logger.info("sending mail to:" + mailTo + " subject:" + subject + " message:" + message);
        try {
            Transport transport = session.getTransport("smtps");
            Preconditions.checkNotNull(session, mailTo, subject, message);

            InternetAddress addressFrom = new InternetAddress(Configuration.getInstance().getMailAddress(),
                    MimeUtility.encodeText(Configuration.getInstance().getMailSender()));
            Preconditions.checkNotNull(transport, addressFrom);

            MimeMessage msg = new MimeMessage(session);
            msg.setHeader("Content-Type", "text/plain; charset=UTF-8");
            Preconditions.checkNotNull(msg);
            msg.setFrom(addressFrom);

            //empfänger
            InternetAddress addressTo = new InternetAddress(mailTo);
            msg.setRecipient(Message.RecipientType.TO, addressTo);
            msg.setSubject(subject, "utf-8");
            msg.setText(message, "utf-8");
            msg.setSentDate(new Date());


            executor.execute(new MailSender(msg));
        } catch (MessagingException | UnsupportedEncodingException e) {
            logger.error("Failed to send Mail to " + mailTo + " Subject: " + subject, e);
        }
    }

    public static void main(String[] args) {
        MailHandler mail = new MailHandler();
        mail.sendMail("krankgesund@gmail.com", "anycook Testmail", "Hat wohl funktioniert");
    }

    /**
     * Authenticator um sich am SMTP-Servern zu authentifizieren
     *
     * @author Jan Grassegger
     */
    private static class SMTPAuthenticator extends Authenticator {
        public PasswordAuthentication getPasswordAuthentication() {
            String username = Configuration.getInstance().getSMTPUser();
            String password = Configuration.getInstance().getSMTPPassword();
            return new PasswordAuthentication(username, password);
        }
    }

    private static class MailSender implements Runnable{
        private static Logger logger = LogManager.getLogger(MailSender.class);

        private final MimeMessage message;

        public MailSender(MimeMessage message){
            this.message = message;
        }

        @Override
        public void run() {
            try {
                Transport.send(message);
                logger.info("sent mail");
            } catch (MessagingException e) {
               logger.error(e, e);
            }
        }
    }

}

