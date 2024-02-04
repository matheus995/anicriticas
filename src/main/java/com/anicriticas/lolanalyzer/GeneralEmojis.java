package com.anicriticas.lolanalyzer;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import static com.anicriticas.lolanalyzer.utils.JsonUtils.getJsonObjectFromUrl;

public class GeneralEmojis {

    public static String getEmojiByName(String emojiName) {
        JSONObject emojis;

        try {
            URL urlGeneralEmojiFile = GeneralEmojis.class.getClassLoader().getResource("general-emojis.json");
            emojis = getJsonObjectFromUrl(urlGeneralEmojiFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (String emoji : emojis.keySet()) {
            if (emoji.equalsIgnoreCase(emojiName)) {
                return emojis.getString(emoji.toLowerCase());
            }
        }
        return "Emoji not found";
    }
}
