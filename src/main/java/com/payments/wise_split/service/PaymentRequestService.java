package com.payments.wise_split.service;

import com.payments.wise_split.dto.PaymentRequestDto;
import com.payments.wise_split.dto.PaymentRequestResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class PaymentRequestService {

    @Autowired
    private final EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(PaymentRequestService.class);

    PaymentRequestService(EmailService emailService){
        this.emailService = emailService;
    }

    @Value("${app.domain.name}")
    private String domain;

    public PaymentRequestResponseDto requestPayment(PaymentRequestDto req){
        try {
            logger.info("Request text:\n{}", req.getText());

            String[] data = req.getText().split("\n");
            logger.info("Data array length: {}", data.length);

            String amount = data[2].replaceAll("[^0-9.]", "");
            Double actualAmount = Double.parseDouble(amount) / 2;
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
            logger.info("PaymentRequestResponseDto created: {}", res);
            return res;
        } catch (Exception e){
            logger.error("Payment request failed", e);
            throw new RuntimeException("payment request failed", e);
        }
    }

    private void requestMoneyFromDebtor(String receiverUpiId,
                                        String debtorName,
                                        String debtorEmail,
                                        String receiverName,
                                        Double amount,
                                        String company){
        try {

            String emailTemplate = emailTemplateLoader("template.html");
            logger.info("Email template loaded successfully. Length: {}", emailTemplate.length());

            String gpayLink = "gpay://upi/pay?pa=" + receiverUpiId +
                    "&pn=" + receiverName +
                    "&am=" + amount +
                    "&cu=INR" +
                    "&tn=" + company;

            String encodedGpayLink = URLEncoder.encode(gpayLink, StandardCharsets.UTF_8);

            String redirectLink = String.format("%s/api/payment/redirect?link=%s", domain, encodedGpayLink);

            String finalEmail = emailTemplate.replace("{{debtorName}}", debtorName)
                    .replace("{{amount}}", amount.toString())
                    .replace("{{receiverUpiId}}", receiverUpiId)
                    .replace("{{for}}", company)
                    .replace("{{receiverName}}", receiverName)
                    .replace("{{redirectLink}}", redirectLink );

            logger.info("Final email content:\n{}", finalEmail);// true = enable HTML

            try {
                logger.info("Attempting to send email to {}", debtorEmail);
                emailService.sendEmail(debtorEmail, "Payment Request", finalEmail);
                logger.info("Email sent successfully to {}", debtorEmail);
            } catch (Exception e) {
                logger.error("Failed to send email", e);
            }

            logger.info("Payment request email sent to {}", debtorEmail);

        } catch (Exception e) {
            logger.error("Failed to send payment email", e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String emailTemplateLoader(String fileName) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("templates/" + fileName)) {
            logger.info("Template exists? {}", in != null);
            if (in == null) {
                throw new RuntimeException("Template not found: " + fileName);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("Failed to load email template {}", fileName, e);
            throw new RuntimeException("Failed to load email template " + fileName, e);
        }
    }
}
