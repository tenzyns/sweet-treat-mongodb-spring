package com.academy.SweetTreatMongo.exception;

public class CourierNotFoundException extends RuntimeException {
    public CourierNotFoundException(String exception) {
        super(exception);
    }
}
