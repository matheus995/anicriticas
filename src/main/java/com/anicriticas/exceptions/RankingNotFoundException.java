package com.anicriticas.exceptions;

public class RankingNotFoundException extends IllegalArgumentException {

    public RankingNotFoundException(String message) {
        super(message);
    }
}
