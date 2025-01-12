package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class NotAuthorizedUserException extends ResponseStatusException {
    public NotAuthorizedUserException(HttpStatusCode statusCode, String message) {
        super(statusCode, message);
    }
}