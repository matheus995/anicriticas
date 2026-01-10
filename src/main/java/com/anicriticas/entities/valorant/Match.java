package com.anicriticas.entities.valorant;

import com.anicriticas.enums.valorant.MatchType;
import com.anicriticas.model.Players;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Match {

    private String id;
    private String startDate;
    private String duration;
    private MatchType matchType;
    private String map;
    private List<Players> players;
    private MatchResult matchResult;

    public void setStartDate(String date) {
        Instant instant = Instant.parse(date);

        ZonedDateTime adjustedDate = instant.atZone(ZoneOffset.ofHours(-3));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
        this.startDate = adjustedDate.format(formatter);
    }

    public void setMatchType(String matchType) {
        this.matchType = MatchType.getMatchByTypeByName(matchType);
    }

    public void setMatchDuration(long durationInMillis) {
        long hours = TimeUnit.MILLISECONDS.toHours(durationInMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationInMillis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationInMillis) % 60;

        StringBuilder duration = new StringBuilder();

        if (hours != 0) {
            duration.append(String.format("%02dh", hours));
        }

        duration.append(String.format("%02dm", minutes));
        duration.append(String.format("%02ds", seconds));

        this.duration = duration.toString();
    }

    public void setPlayers(List<Players> players) {
        players.reversed().sort(Comparator.comparingInt(p -> p.getPlayerStats().getScore()));
        this.players = players;
    }

}
