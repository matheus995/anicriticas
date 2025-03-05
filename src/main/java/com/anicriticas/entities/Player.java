package com.anicriticas.entities;

import com.anicriticas.enums.Ranking;
import com.anicriticas.enums.Region;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Player {

    private String pUUID;
    private String summonerId;
    private String riotId;
    private String riotNickName;
    private Region region;
    private String matchId;
    private Ranking ranking;
    private int accountLevel;

}
