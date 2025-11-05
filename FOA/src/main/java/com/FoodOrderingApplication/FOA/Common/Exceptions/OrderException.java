package com.FoodOrderingApplication.FOA.Common.Exceptions;

public class OrderException extends RuntimeException {
    public OrderException(String message) {
        super(message);
    }
}