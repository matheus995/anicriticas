package com.anicriticas;

import com.anicriticas.discord.MatchFinder;
import com.anicriticas.entities.match.lol.LeagueOfLegendsMatch;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;

import static com.anicriticas.utils.FileUtils.getJsonFromUrl;

public class TestMatchDeserialization {
    public static void main(String[] args) {
        LeagueOfLegendsMatch lolMatch;
        try {
            URL urlGeneralEmojiFile = MatchFinder.class.getClassLoader().getResource("match.json");
            ObjectMapper objectMapper = new ObjectMapper();
            lolMatch = objectMapper.readValue(getJsonFromUrl(urlGeneralEmojiFile), LeagueOfLegendsMatch.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(lolMatch);
    }
}
