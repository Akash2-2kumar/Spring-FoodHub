package com.foodhub.foodhub.Exception;

public class RestaurantDoesNotExistException extends RuntimeException {

    public RestaurantDoesNotExistException(String message) {
        super(message);
    }
}