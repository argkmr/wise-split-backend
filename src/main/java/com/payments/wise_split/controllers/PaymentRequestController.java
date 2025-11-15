package com.payments.wise_split.controllers;

import com.payments.wise_split.dto.PaymentRequestDto;
import com.payments.wise_split.dto.PaymentRequestResponseDto;
import com.payments.wise_split.service.PaymentRequestService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/api/payment")
public class PaymentRequestController {

    private final PaymentRequestService _paymentRequestService;

    @Autowired
    PaymentRequestController(PaymentRequestService paymentRequestService){
        _paymentRequestService = paymentRequestService;
    }

    @PostMapping("/request")
    public ResponseEntity<PaymentRequestResponseDto> paymentRequest(@RequestBody PaymentRequestDto request){
        PaymentRequestResponseDto response = _paymentRequestService.requestPayment(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/redirect")
    public void redirectToGpay(@RequestParam String link, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.getWriter().write(
                "<html><body>" +
                        "<p>Redirecting to payment…</p>" +
                        "<script>window.location.href = '" + link + "';</script>" +
                        "</body></html>"
        );
    }
}
