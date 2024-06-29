package ru.gb.SpringTesting.exception;

public class UserIssueLimitExceededException extends RuntimeException {
    public UserIssueLimitExceededException(String message) {
        super(message);
    }
}
