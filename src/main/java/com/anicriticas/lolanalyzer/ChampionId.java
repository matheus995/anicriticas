package com.anicriticas.lolanalyzer;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class ChampionId {

    public static String getChampionById(String championId) {
        JSONObject champions;

        try {
            URL urlChampionsIdFile = ChampionId.class.getClassLoader().getResource("champions.json");
            champions = getJsonObjectFromUrl(urlChampionsIdFile).getJSONObject("data");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (String championName : champions.keySet()) {
            JSONObject champion = champions.optJSONObject(championName);
            if (champion.getString("key").equals(championId)) {
                return champion.getString("name");
            }
        }
        return "Champion not found";
    }

    public static JSONObject getJsonObjectFromUrl(URL url) throws IOException {
        String json = IOUtils.toString(url, StandardCharsets.UTF_8);
        return new JSONObject(json);
    }
}
