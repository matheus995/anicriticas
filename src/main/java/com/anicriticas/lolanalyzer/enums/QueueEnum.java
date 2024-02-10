package com.anicriticas.lolanalyzer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum QueueEnum {

    RANKED("Ranked"),
    FLEX("Flex"),
    ARAM("Aram"),
    CLASH("Clash"),
    NORMAL_GAME_QUICK_PLAY("Normal Game (Quick Play)"),
    NORMAL_GAME_DRAFT_PICK("Normal Game (Draft Pick)"),
    ARENA("Arena"),
    URF("URF"),
    UNKNOWN_GAME_TYPE("Unknown Game Type");

    private final String queueDescription;
}
