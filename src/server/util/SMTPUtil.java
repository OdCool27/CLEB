package util;


import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.apache.logging.log4j.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class SMTPUtil {
    private static final Logger logger = LogManager.getLogger(SMTPUtil.class);
    private static final Properties smtpProps = new Properties();
    private static final String propertiesFilePath = "src/main/resources/smtp.properties";

    static {
        try (FileInputStream input = new FileInputStream(propertiesFilePath)) {
            smtpProps.load(input);
            logger.info("SMTP Configuration Loaded Successfully");
        } catch (IOException e) {
            logger.error("Failed to load SMTP config file: {}", propertiesFilePath, e);
        }
    }

    public static void sendEmail(String to, String subject, String content) {
        String username = smtpProps.getProperty("mail.username");
        String password = smtpProps.getProperty("mail.appPassword");

        Properties props = new Properties();
        props.put("mail.smtp.auth", smtpProps.getProperty("mail.smtp.auth"));
        props.put("mail.smtp.starttls.enable", smtpProps.getProperty("mail.smtp.starttls.enable"));
        props.put("mail.smtp.host", smtpProps.getProperty("mail.smtp.host"));
        props.put("mail.smtp.port", smtpProps.getProperty("mail.smtp.port"));

        Session session = Session.getInstance(props, new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setContent(content, "text/html; charset=utf-8");

            Transport.send(message);
            logger.info("Email Sent to {}", to);
        } catch (MessagingException e) {
            logger.error("Failed to send email to {}", to, e);
        }
    }
}
