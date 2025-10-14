package com.anicriticas.enums;

import com.anicriticas.exceptions.TeamNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum TeamSide {

    BLUE(100),
    RED(200);

    private final int teamId;

    public static TeamSide getTeamById(int teamId) {
        return Arrays.stream(values())
                .filter(team -> team.getTeamId() == teamId)
                .findFirst()
                .orElseThrow(() -> new TeamNotFoundException("Team side not found: " + teamId));
    }
}
