package com.payments.wise_split.controllers;

import com.payments.wise_split.dto.PaymentRequestDto;
import com.payments.wise_split.dto.PaymentRequestResponseDto;
import com.payments.wise_split.service.PaymentRequestService;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/api/payment")
public class PaymentRequestController {

    private final PaymentRequestService _paymentRequestService;
    private static final Logger logger = LoggerFactory.getLogger(PaymentRequestService.class);

    @Autowired
    PaymentRequestController(PaymentRequestService paymentRequestService){
        _paymentRequestService = paymentRequestService;
    }

    @PostMapping("/request")
    public ResponseEntity<PaymentRequestResponseDto> paymentRequest(@RequestBody PaymentRequestDto request){
        PaymentRequestResponseDto response = _paymentRequestService.requestPayment(request);

        // Log message safely with placeholder
        logger.info("Payment request response message: {}", response.getMessage());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/redirect")
    public void redirectToGpay(@RequestParam String link, HttpServletResponse response) throws IOException {
        String decodedLink = java.net.URLDecoder.decode(link, StandardCharsets.UTF_8);
        response.setContentType("text/html");
        response.getWriter().write(
                "<html><body>" +
                        "<p>Redirecting to payment…</p>" +
                        "<script>window.location.href = '" + decodedLink + "';</script>" +
                        "</body></html>"
        );
    }
}
