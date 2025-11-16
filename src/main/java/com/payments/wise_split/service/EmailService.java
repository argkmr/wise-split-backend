package com.payments.wise_split.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${sendgrid.api.key}")
    private String sendgridApiKey;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendEmail(String toEmail, String subject, String htmlContent) {
        Email from = new Email(fromEmail, "Wise Split");
        Email to = new Email(toEmail);
        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendgridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            logger.info("Email sent! Status: {}", response.getStatusCode());
        } catch (IOException ex) {
            logger.error("Failed to send email via SendGrid", ex);
            throw new RuntimeException(ex);
        }
    }
}
