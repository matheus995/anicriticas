package com.anicriticas.entities.match.lol;

import com.anicriticas.enums.TeamSide;
import com.anicriticas.utils.ChampionUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BannedChampion {

    private String champion;
    private TeamSide teamSide;

    @JsonProperty("championId")
    public void setChampion(String championId) {
        this.champion = ChampionUtils.getChampionById(championId);
    }

    @JsonProperty("teamId")
    public void setTeamSide(int teamId) {
        this.teamSide = TeamSide.getTeamById(teamId);
    }
}
