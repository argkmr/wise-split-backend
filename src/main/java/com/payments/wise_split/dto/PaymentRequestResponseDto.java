package com.payments.wise_split.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestResponseDto {
    private boolean success;
    private String message;
    private String amountSent;
}
