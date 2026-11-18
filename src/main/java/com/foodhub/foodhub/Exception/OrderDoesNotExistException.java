package com.foodhub.foodhub.Exception;

public class OrderDoesNotExistException extends RuntimeException {

    public OrderDoesNotExistException(String message) {
        super(message);
    }
}