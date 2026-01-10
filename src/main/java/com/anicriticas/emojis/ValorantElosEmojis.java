package com.anicriticas.emojis;

import org.json.JSONObject;

import java.net.URL;

import static com.anicriticas.utils.FileUtils.getJsonObjectFromUrl;
import static com.anicriticas.utils.FileUtils.getResourceUrlByFileName;

public class ValorantElosEmojis {

    public static String getEmojiByElo(String elo) {
        JSONObject emojis;

        URL urlElosEmojiFile = getResourceUrlByFileName("emojis/valorant-elos-emojis.json");
        emojis = getJsonObjectFromUrl(urlElosEmojiFile);

        for (String eloString : emojis.keySet()) {
            if (eloString.equalsIgnoreCase(elo)) {
                return emojis.getString(elo);
            }
        }
        return "Elo Emoji not found";
    }
}
