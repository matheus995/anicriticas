package com.anicriticas.lolanalyzer;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import static com.anicriticas.lolanalyzer.utils.JsonUtils.getJsonObjectFromUrl;

public class ChampionId {

    public static String getChampionById(String championId) {
        JSONObject champions;

        try {
            URL urlChampionsIdFile = ChampionId.class.getClassLoader().getResource("champions.json");
            champions = getJsonObjectFromUrl(urlChampionsIdFile).getJSONObject("data");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
