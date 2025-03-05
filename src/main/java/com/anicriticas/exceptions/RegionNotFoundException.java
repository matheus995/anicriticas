package com.anicriticas.exceptions;

public class RegionNotFoundException extends IllegalArgumentException {

    public RegionNotFoundException(String message) {
        super(message);
    }
}
