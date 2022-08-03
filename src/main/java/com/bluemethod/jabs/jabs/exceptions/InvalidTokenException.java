package com.bluemethod.jabs.jabs.exceptions;

public class InvalidTokenException extends Exception {
    String message;
    public InvalidTokenException(String str) {
        message = str;
    }

    public String toString() {
        return ("Exception Occurred: " + message);
    }
}
