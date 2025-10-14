package com.anicriticas.emojis;

import org.json.JSONObject;

import java.net.URL;

import static com.anicriticas.utils.FileUtils.getJsonObjectFromUrl;
import static com.anicriticas.utils.FileUtils.getResourceUrlByFileName;

public class ChampionsEmojis {

    public static String getEmojiByChampionName(String championName) {
        JSONObject emojis;

        URL urlChampionsEmojiFile = getResourceUrlByFileName("champions-emojis.json");
        emojis = getJsonObjectFromUrl(urlChampionsEmojiFile);

        if (championName.equalsIgnoreCase("NoBan")) {
            return emojis.getString("NoBan");
        }

        for (String champion : emojis.keySet()) {
            if (champion.equalsIgnoreCase(championName)) {
                return emojis.getString(champion);
            }
        }
        return "Champion Emoji not found";
    }
}
