package com.backend.service;

import static com.backend.constant.EmailConstant.*;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Message.RecipientType;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.mail.smtp.SMTPTransport;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void sendSuccessfullyRegisterMessageForAdmin(String firstName, String email, String password)
            throws AddressException, MessagingException {
        Message message = createEmail(firstName, password, email);
        SMTPTransport smtpTransport = (SMTPTransport) getEmailSession()
                .getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
        smtpTransport.connect(GMAIL_SMTP_SERVER, USERNAME, PASSWORD);
        smtpTransport.sendMessage(message, message.getAllRecipients());
        smtpTransport.close();
    }

    public void sendSuccessfullyRegisterMessageForUser(String firstName, String email)
            throws AddressException, MessagingException {
        Message message = createEmailForUser(firstName, email);
        SMTPTransport smtpTransport = (SMTPTransport) getEmailSession()
                .getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
        smtpTransport.connect(GMAIL_SMTP_SERVER, USERNAME, PASSWORD);
        smtpTransport.sendMessage(message, message.getAllRecipients());
        smtpTransport.close();
    }

    private Message createEmailForUser(String firstName, String email) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(RecipientType.TO, InternetAddress.parse(email, false));
        message.setRecipients(RecipientType.CC, InternetAddress.parse(CC_EMAIL, false));
        message.setSubject(EMAIL_SUBJECT);
        message.setText("Hello " + firstName
                + "\n\n Your account have successfully registered.\n\nPlease contact our administration support for more details.\n\nThank you!");
        message.setSentDate(new Date());
        message.saveChanges();
        return message;
    }

    private Message createEmail(String firstName, String password, String email)
            throws AddressException, MessagingException {
        Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(RecipientType.TO, InternetAddress.parse(email, false));
        message.setRecipients(RecipientType.CC, InternetAddress.parse(CC_EMAIL, false));
        message.setSubject(EMAIL_SUBJECT);
        message.setText("Hello " + firstName
                + "\n\n You have successfully registered your account.\n\nYour temporary password is: " + password
                + "\n\nPlease login with this password and reset your password.\n\nThank you!");
        message.setSentDate(new Date());
        message.saveChanges();
        return message;
    }

    private Session getEmailSession() {
        Properties properties = System.getProperties();
        properties.put(SMTP_HOST, GMAIL_SMTP_SERVER);
        properties.put(SMTP_AUTH, true);
        properties.put(SMTP_PORT, DEFAULT_PORT);
        properties.put(SMTP_STARTTLS_ENABLE, true);
        properties.put(SMTP_STARTTLS_REQUIRED, true);
        return Session.getInstance(properties, null);
    }
    
}
