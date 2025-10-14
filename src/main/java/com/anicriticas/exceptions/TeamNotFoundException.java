package com.anicriticas.exceptions;

public class TeamNotFoundException extends IllegalArgumentException {

    public TeamNotFoundException(String message) {
        super(message);
    }
}
