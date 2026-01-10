package com.anicriticas.entities.valorant;

import com.anicriticas.enums.valorant.Region;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Player {

    private String pUUID;
    private String name;
    private String tag;
    private Region region;
    private String team;
    private String agent;
    private PlayerStats playerStats;
    private String ranking;

    public static Player getPlayerInList(Player playerToFind, List<Player> playerList) {
        for (Player player : playerList) {
            if (player.getPUUID().equalsIgnoreCase(playerToFind.getPUUID())) {
                return player;
            }
        }

        return playerToFind;
    }

}
