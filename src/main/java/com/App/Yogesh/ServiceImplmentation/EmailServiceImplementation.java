package com.App.Yogesh.ServiceImplmentation;

import com.App.Yogesh.ResponseDto.MailBody;
import com.App.Yogesh.Services.EmaillsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailServiceImplementation implements EmaillsService {

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final JavaMailSender javaMailSender;

    // Constructor injection for JavaMailSender
    public EmailServiceImplementation(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendSimpleMessage(MailBody mailBody) {
        // Creating a SimpleMailMessage to send a plain text email
        SimpleMailMessage message = new SimpleMailMessage();

        // Setting the recipient email address, subject, and body of the email
        message.setTo(mailBody.to());
        message.setFrom(fromEmail);  // This is the sender's email, retrieved from application.properties
        message.setSubject(mailBody.subject());
        message.setText(mailBody.text());

        try {
            // Sending the email using JavaMailSender
            javaMailSender.send(message);
            log.info("Email sent successfully to {}", mailBody.to());  // Logging successful email sending
        } catch (Exception e) {
            // Logging error if the email fails to send
            log.error("Error occurred while sending email to {}: {}", mailBody.to(), e.getMessage());
        }
    }
}
