package com.anicriticas.emojis;

import org.json.JSONObject;

import java.net.URL;

import static com.anicriticas.utils.FileUtils.getJsonObjectFromUrl;
import static com.anicriticas.utils.FileUtils.getResourceUrlByFileName;

public class GeneralEmojis {

    public static String getEmojiByName(String emojiName) {
        JSONObject emojis;

        URL urlGeneralEmojiFile = getResourceUrlByFileName("general-emojis.json");
        emojis = getJsonObjectFromUrl(urlGeneralEmojiFile);

        for (String emoji : emojis.keySet()) {
            if (emoji.equalsIgnoreCase(emojiName)) {
                return emojis.getString(emoji.toLowerCase());
            }
        }
        return "Emoji not found";
    }
}
