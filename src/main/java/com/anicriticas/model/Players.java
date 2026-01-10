package com.anicriticas.model;

import com.anicriticas.entities.valorant.PlayerStats;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Entity
@Table (name = "players")
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Players implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private UUID puuid;

    @Column (nullable = false)
    private String name;

    @Column (nullable = false)
    private String tag;

    @ManyToOne
    @JoinColumn(name = "region_id", nullable = false)
    private Regions region;

    @Transient
    private String team;

    @Transient
    private String agent;

    @Transient
    private PlayerStats playerStats;

    @Transient
    private String ranking;

    public static Players getPlayerInList(Players playerToFind, List<Players> playerList) {
        for (Players player : playerList) {
            if (player.getPuuid().equals(playerToFind.getPuuid())) {
                return player;
            }
        }

        return playerToFind;
    }
}
