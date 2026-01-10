package com.anicriticas.entities.valorant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchResult {

    private String winner;
    private int roundsWinner;
    private int roundsLoser;

}
