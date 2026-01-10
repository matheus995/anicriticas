package com.anicriticas.enums.valorant;

import com.anicriticas.exceptions.RegionNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum MatchType {

    COMPETITIVE("Competitive"),
    CUSTOM("Custom"),
    DEATHMATCH("Deathmatch"),
    SPIKERUSH("Spikerush"),
    UNRATED("Unrated");

    private final String typeName;

    public static MatchType getMatchByTypeByName(String typeName) {
        return Arrays.stream(values())
                .filter(type -> type.getTypeName().equalsIgnoreCase(typeName))
                .findFirst()
                .orElseThrow(() -> new RegionNotFoundException("Match Type not found: " + typeName));
    }

    public static MatchType getMatchTypeByEnumName(String enumName) {
        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(enumName))
                .findFirst()
                .orElseThrow(() -> new RegionNotFoundException("Match Type not found: " + enumName));
    }
}