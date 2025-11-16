package com.payments.wise_split.service;

import com.payments.wise_split.dto.PaymentRequestDto;
import com.payments.wise_split.dto.PaymentRequestResponseDto;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Service
public class PaymentRequestService {

    @Autowired
    private final JavaMailSender mailSender;

    PaymentRequestService(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    @Value("${app.domain.name}")
    private String domain;

    @Value("${app.domain.port}")
    private String port;

    @Value("${app.environment}")
    private String env;

    public PaymentRequestResponseDto requestPayment(PaymentRequestDto req){
        String[] data = req.getText().split("\n");
        String amount = data[2].replaceAll("[^0-9.]", "");
        Double actualAmount = Double.parseDouble(amount)/2;
        String company = data[1];
        requestMoneyFromDebtor(req.getReceiverUpiId(),
                                req.getDebtorName(),
                                req.getDebtorEmail(),
                                req.getReceiverName(),
                                actualAmount,
                                company);
        PaymentRequestResponseDto res = PaymentRequestResponseDto.builder()
                .message(amount + " has been sent to you please verify")
                .success(true)
                .amountSent(amount)
                .build();
        return res;
    }

    private void requestMoneyFromDebtor(String receiverUpiId,
                                        String debtorName,
                                        String debtorEmail,
                                        String receiverName,
                                        Double amount,
                                        String company){
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            String emailTemplate = emailTemplateLoader("template.html");

            String gpayLink = "gpay://upi/pay?pa=" + receiverUpiId +
                    "&pn=" + receiverName +
                    "&am=" + amount +
                    "&cu=INR" +
                    "&tn=" + company;

            String encodedGpayLink = URLEncoder.encode(gpayLink, StandardCharsets.UTF_8);

            String devRedirectLink = String.format("http://%s:%s/api/payment/redirect?link=%s", domain, port, encodedGpayLink);
            String prodRedirectLink = String.format("http://%s/api/payment/redirect?link={{%s}}", domain, encodedGpayLink);

            String finalEmail = emailTemplate.replace("{{debtorName}}", debtorName)
                    .replace("{{amount}}", amount.toString())
                    .replace("{{receiverUpiId}}", receiverUpiId)
                    .replace("{{for}}", company)
                    .replace("{{receiverName}}", receiverName)
                    .replace("{{redirectLink}}", Objects.equals(env, "PROD") ? prodRedirectLink : devRedirectLink );


            helper.setTo(debtorEmail);
            helper.setSubject("Payment Request");
            helper.setText(finalEmail, true);  // true = enable HTML

            mailSender.send(message);

        }catch (Exception e){
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String emailTemplateLoader(String fileName){
        try{
            ClassPathResource resource = new ClassPathResource("templates/" + fileName);
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        }catch (Exception e){
            throw new RuntimeException("Failed to load email template" + fileName, e);
        }
    }
}
