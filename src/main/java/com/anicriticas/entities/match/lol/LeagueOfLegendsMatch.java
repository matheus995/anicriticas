package com.anicriticas.entities.match.lol;

import com.anicriticas.entities.Player;
import com.anicriticas.entities.match.Match;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LeagueOfLegendsMatch extends Match {

    private List<Player> winners;
    private List<BannedChampion> bannedChampions;
}
