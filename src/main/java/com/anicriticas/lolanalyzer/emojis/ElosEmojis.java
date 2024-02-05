package com.anicriticas.lolanalyzer.emojis;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import static com.anicriticas.lolanalyzer.utils.JsonUtils.getJsonObjectFromUrl;

public class ElosEmojis {

    public static String getEmojiByElo(String elo) {
        JSONObject emojis;

        try {
            URL urlElosEmojiFile = ElosEmojis.class.getClassLoader().getResource("elos-emojis.json");
            emojis = getJsonObjectFromUrl(urlElosEmojiFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (String eloString : emojis.keySet()) {
            if (eloString.equalsIgnoreCase(elo)) {
                return emojis.getString(elo.toLowerCase());
            }
        }
        return "Elo Emoji not found";
    }
}
