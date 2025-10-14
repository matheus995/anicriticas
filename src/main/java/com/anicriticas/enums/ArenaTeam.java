package com.anicriticas.enums;

import com.anicriticas.exceptions.ArenaTeamNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ArenaTeam {

    POROS(1),
    MINIONS(2),
    SCUTTLES(3),
    KRUGS(4),
    RAPTOR(5),
    SENTINEL(6),
    WOLVE(7),
    GROMP(8);

    private final int teamId;

    public static ArenaTeam getTeamById(int teamId) {
        return Arrays.stream(values())
                .filter(team -> team.getTeamId() == teamId)
                .findFirst()
                .orElseThrow(() -> new ArenaTeamNotFoundException("Arena team id not found: " + teamId));
    }
}
