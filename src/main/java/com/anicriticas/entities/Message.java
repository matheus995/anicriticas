package com.anicriticas.entities;

import com.anicriticas.entities.match.Match;
import discord4j.common.util.Snowflake;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {

    private Snowflake id;
    private Match match;
}
