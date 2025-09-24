package com.example.BusSystem.service.exception;

public class InactiveLineException extends RuntimeException{
    public InactiveLineException(String message) {
        super(message);
    }
}
