package com.payments.wise_split.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {
    private String text;
    private String receiverUpiId;
    private String debtorEmail;
    private String receiverName;
    private String debtorName;
}
