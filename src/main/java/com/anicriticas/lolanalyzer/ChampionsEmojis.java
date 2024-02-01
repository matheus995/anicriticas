package com.anicriticas.lolanalyzer;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ChampionsEmojis {

    public static String getEmojiByChampionName(String championName) {
        JSONObject emojis;

        try {
            URL urlChampionsEmojiFile = ChampionsEmojis.class.getClassLoader().getResource("champions-emojis.json");
            emojis = getJsonObjectFromUrl(urlChampionsEmojiFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (String champion : emojis.keySet()) {
            if (champion.equals(championName)) {
                return emojis.getString(champion);
            }
        }
        return "Champion Emoji not found";
    }

    public static JSONObject getJsonObjectFromUrl(URL url) throws IOException {
        String json = IOUtils.toString(url, StandardCharsets.UTF_8);
        return new JSONObject(json);
    }
}
