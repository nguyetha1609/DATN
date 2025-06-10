package org.o7planning.project_04.utils;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;
    private static final String USER = "youremail@gmail.com";
    private static final String PASS = "your_app_password"; // dùng App Password

    public static void sendCode(final String toEmail, final String code) {
        new Thread(() -> {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(USER, PASS);
                }
            });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(USER));
                message.setRecipients(
                        Message.RecipientType.TO,
                        InternetAddress.parse(toEmail)
                );
                message.setSubject("Mã xác nhận của bạn");
                message.setText("Mã xác nhận của bạn là: " + code +
                        "\nNếu không phải bạn, hãy bỏ qua email này.");
                Transport.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
