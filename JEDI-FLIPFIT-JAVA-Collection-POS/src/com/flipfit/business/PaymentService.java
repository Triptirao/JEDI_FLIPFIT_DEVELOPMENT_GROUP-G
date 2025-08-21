//package com.flipfit.business;
//
//import com.flipfit.bean.Payment;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//public class PaymentService {
//
//    public Payment processPayment(double amount, String customerId, String gymId, String slotId) {
//        String paymentId = UUID.randomUUID().toString();
//
//        // Use a string literal for the status.
//        String status = "Success";
//
//        Payment newPayment = new Payment(
//                paymentId,
//                amount,
//                LocalDateTime.now(),
//                customerId,
//                gymId,
//                slotId,
//                status
//        );
//
//        System.out.println("Payment processed with ID: " + paymentId);
//        return newPayment;
//    }
//
//    /**
//     * Checks if a payment was successful based on its status.
//     *
//     * @param paymentId The unique ID of the payment to check.
//     * @return true if the payment's status is "Success", false otherwise.
//     */
//    public boolean isPaymentSuccessful(String paymentId) {
//        Payment payment = getPaymentDetails(paymentId);
//        if (payment != null) {
//            // Compare the transactionStatus with the string "Success".
//            return payment.getTransactionStatus().equalsIgnoreCase("Success");
//        }
//        return false;
//    }
//
//    public Payment getPaymentDetails(String paymentId) {
//        return null;
//    }
//
//    public List<Payment> getPaymentsByCustomerId(String customerId) {
//
//        return null;
//    }
//}