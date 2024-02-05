package com.anicriticas.lolanalyzer.emojis;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import static com.anicriticas.lolanalyzer.utils.JsonUtils.getJsonObjectFromUrl;

public class ChampionsEmojis {

    public static String getEmojiByChampionName(String championName) {
        JSONObject emojis;

        try {
            URL urlChampionsEmojiFile = ChampionsEmojis.class.getClassLoader().getResource("champions-emojis.json");
            emojis = getJsonObjectFromUrl(urlChampionsEmojiFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (championName.equalsIgnoreCase("NoBan")) {
            return emojis.getString("NoBan");
        }

        for (String champion : emojis.keySet()) {
            if (champion.equals(championName)) {
                return emojis.getString(champion);
            }
        }
        return "Champion Emoji not found";
    }
}
