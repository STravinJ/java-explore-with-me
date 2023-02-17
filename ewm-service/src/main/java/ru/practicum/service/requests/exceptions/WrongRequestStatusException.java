package ru.practicum.service.requests.exceptions;

public class WrongRequestStatusException extends RuntimeException {

    public WrongRequestStatusException(String message) {
        super(message);
    }
}
