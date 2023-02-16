package ru.practicum.service.requests.exceptions;

public class RequestNotFoundException extends Exception {

    public RequestNotFoundException(String message) {
        super(message);
    }
}
