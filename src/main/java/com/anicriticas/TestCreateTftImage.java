package com.anicriticas;

import com.anicriticas.entities.match.tft.Character;
import com.anicriticas.utils.CreateTftImageUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.anicriticas.utils.FileUtils.getJsonObjectFromUrl;

public class TestCreateTftImage {
    public static void main(String[] args) {
        JSONObject finishedMatch;
        URL urlGeneralEmojiFile = TestCreateTftImage.class.getClassLoader().getResource("match.json");
        finishedMatch = getJsonObjectFromUrl(urlGeneralEmojiFile);

        String puuid = "r2FIxYd2RU2VPdb7DnmhrfY8fpCtdpCBx9_z8Gf5MTHqUPFU3WrN64PCNGTG1-TF9FRIOAoxYCJ0lw";

        createTftImage(finishedMatch, puuid);
    }

    private static void createTftImage(JSONObject finishedMatch, String participantToFind) {
        JSONArray matchParticipants = finishedMatch.getJSONObject("info").getJSONArray("participants");

        JSONObject participant = CreateTftImageUtils.getParticipantToCreateImage(participantToFind, matchParticipants);

        List<Character> characterList = new ArrayList<>();
        for (int j = 0; j < Objects.requireNonNull(participant).getJSONArray("units").toList().size(); j++) {
            JSONObject unit = participant.getJSONArray("units").getJSONObject(j);

            String characterName = unit.getString("character_id").substring(unit.getString("character_id").indexOf("_") + 1);

            int tier = unit.getInt("tier");
            int rarity = unit.getInt("rarity");

            List<String> items = new ArrayList<>();
            for (int i = 0; i < unit.getJSONArray("itemNames").toList().size(); i++) {
                String item = (String) unit.getJSONArray("itemNames").get(i);
                items.add(item);
            }
//                System.out.printf("%s | %d | %d | %s\n", characterName, tier, rarity, items);

            characterList.add(new Character(characterName, tier, rarity, items));
        }

        CreateTftImageUtils.createImage(participant, characterList);
    }
}
