package com.anicriticas.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

import static com.anicriticas.utils.FileUtils.getJsonObjectFromUrl;
import static com.anicriticas.utils.FileUtils.getResourceUrlByFileName;

public class TftUtils {

    private static final String TRAITS_FILE = "traits.json";

    public static StringBuilder getUnitsFromParticipant(JSONObject participant) {
        JSONArray traits = participant.getJSONArray("traits");
        StringBuilder strBuilderUnits = new StringBuilder();

        for (int i = 0; i < traits.toList().size(); i++) {
            JSONObject unit = traits.getJSONObject(i);

            if (unit.getInt("tier_current") > 0) {
                String trait = getTraitName(unit.getString("name"));
                int qtd = unit.getInt("num_units");

                strBuilderUnits.append(String.format("%s %d | ", trait, qtd));
            }
        }

        return strBuilderUnits;
    }

    public static String getTraitName(String trait) {
        JSONObject traits;
        String traitName;

        URL url = getResourceUrlByFileName(TRAITS_FILE);
        traits = getJsonObjectFromUrl(url).getJSONObject("data");

        try {
            JSONObject jsonTrait = traits.getJSONObject(trait);
            traitName = jsonTrait.getString("name");
        } catch (JSONException e) {
           throw new RuntimeException(e);
        }

        return traitName;
    }
}
