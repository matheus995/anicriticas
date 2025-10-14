package com.anicriticas.utils;

import org.json.JSONObject;

import java.net.URL;

import static com.anicriticas.utils.FileUtils.getJsonObjectFromUrl;
import static com.anicriticas.utils.FileUtils.getResourceUrlByFileName;

public class ChampionUtils {

    public static String getChampionById(String championId) {
        JSONObject champions;

        URL urlChampionsIdFile = getResourceUrlByFileName("champions.json");
        champions = getJsonObjectFromUrl(urlChampionsIdFile).getJSONObject("data");

        if (championId.equals("-1")) {
            return "NoBan";
        }

        for (String championName : champions.keySet()) {
            JSONObject champion = champions.optJSONObject(championName);
            if (champion.getString("key").equals(championId)) {
                return champion.getString("name");
            }
        }
        return "Champion not found";
    }
}
