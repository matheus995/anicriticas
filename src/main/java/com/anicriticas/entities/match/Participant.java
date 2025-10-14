package com.anicriticas.entities.match;

import com.anicriticas.enums.TeamSide;
import com.anicriticas.utils.ChampionUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Participant {

    private String puuid;
    private String riotId;
    private String summonerId;
    private TeamSide teamSide;
    private String champion;
    private int profileIconId;

    @JsonProperty("teamId")
    public void setTeamSide(int teamId) {
        this.teamSide = TeamSide.getTeamById(teamId);
    }

    @JsonProperty("championId")
    public void setChampion(String championId) {
        this.champion = ChampionUtils.getChampionById(championId);
    }
}
