package com.anicriticas;

import com.anicriticas.discord.MatchFinder;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.Objects;

import static com.anicriticas.utils.FileUtils.getJsonObjectFromUrl;

public class TestGetTraits {
    public static void main(String[] args) {
        JSONObject traits;

        URL url = getResourceUrlByFileName("traits2.json");
        traits = getJsonObjectFromUrl(url).getJSONObject("data");

        try {
            JSONObject trait = traits.getJSONObject("TFT14_BallisTek");
            trait.getString("name");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        System.out.println(traits);
    }

    public static URL getResourceUrlByFileName(String fileName) {
        URL url = MatchFinder.class.getClassLoader().getResource("traits2.json");

        if (Objects.isNull(url)) {
            throw new RuntimeException("File not found in resources: " + fileName);
        }

        return url;
    }
}
