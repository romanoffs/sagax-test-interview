package com.sagax.shop.exception;

// CASE 29: Extends Exception (checked) instead of RuntimeException.
// Forces 'throws' declarations up the call chain.
// CASE 8: Interacts badly with @Transactional — Spring does NOT rollback
// on checked exceptions by default, so the transaction commits despite the error.
public class PaymentException extends Exception {
    public PaymentException(String message) {
        super(message);
    }

    public PaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}
