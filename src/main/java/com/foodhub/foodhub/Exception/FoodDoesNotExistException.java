package com.foodhub.foodhub.Exception;

public class FoodDoesNotExistException extends RuntimeException {

    public FoodDoesNotExistException(String message) {
        super(message);
    }
}