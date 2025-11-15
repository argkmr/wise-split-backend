//package com.payments.wise_split.entities;
//
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.bson.types.ObjectId;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;
//
//import java.util.Date;
//
//@Document("payment")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class Payment {
//    @Id
//    private ObjectId _id;
//
//    private Date date;
//    private String company;
//    private String description;
//    private PaymentInfo paymentInfo;
//
//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class PaymentInfo{
//        private String paymentRequestUpi;
//        private String paymentFromUpi;
//        private String amount;
//    }
//}
