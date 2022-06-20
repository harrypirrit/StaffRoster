/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author liam
 */
public class EmailService {

    private final Session session;

    public EmailService() {
        // SMTP server details
        Properties p = System.getProperties();
        p.put("mail.smtp.host", "premium80.web-hosting.com");
        p.put("mail.smtp.socketFactory.port", "465");
        p.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        p.put("mail.smtp.auth", "true");
        p.put("mail.smtp.port", "465");

        // SMTP server authentication
        session = Session.getInstance(p,
                new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("mail@debeer.nz", "o6WiCge1FWUBR2aIH7");
            }
        });

    }

    /**
     * This method sends an email to the recipient with the body
     *
     * @param recipient
     * @param body
     * @return
     */
    public boolean SendEmail(String recipient, String subject, String body) {
        try {
            // Set email content
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("mail@debeer.nz"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            msg.setSubject(subject);
            msg.setContent(body, "text/html");

            // Send email
            Transport.send(msg);
            System.out.print("Message has been sent.");
        } catch (AddressException ex) {
            ex.printStackTrace();
            return false;
        } catch (MessagingException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
}
