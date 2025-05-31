package org.stock.exceptions;

public class NonUniqueIdException extends RuntimeException {
    public NonUniqueIdException(String message) {
        super(message);
    }
}