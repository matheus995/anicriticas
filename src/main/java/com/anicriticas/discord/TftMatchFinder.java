package com.anicriticas.discord;

import com.anicriticas.discord.messagebuilder.MessageBuilder;
import com.anicriticas.entities.tft.Character;
import com.anicriticas.enums.Region;
import com.anicriticas.service.LolAPIService;
import com.anicriticas.service.RiotAPIService;
import com.anicriticas.service.TftAPIService;
import com.anicriticas.utils.CreateTftImageUtils;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import discord4j.rest.util.Color;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static com.anicriticas.utils.JsonUtils.getJsonArrayFromUrl;

@Component
public class TftMatchFinder {

    @Autowired
    GatewayDiscordClient client;

    private final TftAPIService tftAPIService = new TftAPIService();

    private Map<String, JSONObject> playersInGame = new HashMap<>();

    private Map<JSONObject, Snowflake> gamesAlreadyMessaged = new HashMap<>();

    private Snowflake messageId = null;

    private final String matchFindChannelId = System.getenv("MATCH_FIND_TFT_CHANNEL_ID");

    // Deixar fixedRate parametrizável
    @Scheduled(fixedRate = 180000)
    public void TftMatchListener() {
        JSONArray playersInfo;

        //TODO buscar buscar.json de players apenas uma vez
        try {
            URL urlGeneralEmojiFile = TftMatchFinder.class.getClassLoader().getResource("match-find-players-tft.json");
            playersInfo = getJsonArrayFromUrl(urlGeneralEmojiFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < playersInfo.toList().size(); i++) {
            JSONObject playerInfo = playersInfo.getJSONObject(i);

            String puuid = playerInfo.getString("puuid");
            Region accountRegion = Region.getByRegionName(playerInfo.getString("region"));

            JSONObject activeGame = tftAPIService.getActiveGamesByPuuid(puuid, accountRegion);

            // If player not in game, check the next player
            if (Objects.isNull(activeGame)) {
                playersInGame.remove(puuid);
                continue;
            }

            // If puuid from player it's in the Map playersInGame
            // And if gameId found is different from gameId that is in the Map, then it's a new game found, do a replacement in the Map
            // If gameId found equals to gameId in the Map, so it's the same game, check the next player
            if (playersInGame.containsKey(puuid)) {
                if (activeGame.getLong("gameId") != playersInGame.get(puuid).getLong("gameId")) {
                    playersInGame.replace(puuid, activeGame);
                }
                continue;
            }

            playersInGame.put(playerInfo.getString("puuid"), activeGame);
        }

        for (Map.Entry<String, JSONObject> gameInfo : playersInGame.entrySet()) {
            boolean messageAlreadySend = false;

            // If the match hasn't yet been notified on discord, then notify
            for (Map.Entry<JSONObject, Snowflake> gameMessaged : gamesAlreadyMessaged.entrySet()) {
                if (gameMessaged.getKey().getLong("gameId") == gameInfo.getValue().getLong("gameId")) {
                    messageAlreadySend = true;
                    break;
                }
            }

            if (messageAlreadySend) {
                continue;
            }

            JSONArray participants = new JSONArray(gameInfo.getValue().getJSONArray("participants"));
            JSONArray participantsRankedInfo = new JSONArray();

            Region matchRegion = Region.getRegionByEnumName(gameInfo.getValue().getString("platformId"));
            for (int i = 0; i < participants.toList().size(); i++) {
                JSONObject participant = participants.getJSONObject(i);

                JSONObject rankedInfo = getRankingTftSolo(tftAPIService.getRankedStats(participant.getString("summonerId"), matchRegion));
                participantsRankedInfo.put(rankedInfo);
            }

            EmbedCreateSpec matchFoundMessageBuilder = EmbedCreateSpec.builder()
                    .color(Color.CYAN)
                    .author("Match Found - TFT", "", "")
                    .addField(MessageBuilder.getMatchFoundInformation(gameInfo.getValue()))
                    .addField(MessageBuilder.getTftMatchFoundPlayers(
                            gameInfo.getValue().getJSONArray("participants"),
                            participantsRankedInfo,
                            true)
                    )
                    .build();

            client.getChannelById(Snowflake.of(matchFindChannelId))
                    .ofType(MessageChannel.class)
                    .flatMap(channel -> channel.createMessage(matchFoundMessageBuilder)
                            .doOnNext(message -> messageId = message.getId()))
                    .block();

            gamesAlreadyMessaged.put(gameInfo.getValue(), messageId);
        }

        // If match finished, edit the original message with the result of the match
        for (Map.Entry<JSONObject, Snowflake> gameMessaged : gamesAlreadyMessaged.entrySet()) {

            Region matchRegion = Region.getRegionByEnumName(gameMessaged.getKey().getString("platformId"));
            String matchId = matchRegion.name() + "_" + gameMessaged.getKey().get("gameId");

            System.out.println("------------------------------------------------------------------------");
            System.out.printf("Pesquisando se partida foi finalizada. MatchId: %s | MessageId: %s\n", matchId, gameMessaged.getValue());

            // Search if match it's already finished
            JSONObject finishedMatch = tftAPIService.getFinishedMatchById(matchId, matchRegion);

            if (Objects.isNull(finishedMatch)) {
                System.out.printf("Partida nao foi finalizada. MatchId: %s | MessageId: %s\n", matchId, gameMessaged.getValue());
            } else {
                System.out.printf("Partida FOI finalizada. MatchId: %s | MessageId: %s\n", matchId, gameMessaged.getValue());
            }

            // If match finished, edit original message
            if (Objects.nonNull(finishedMatch)) {
                JSONObject player = getPlayerToCreateImage(finishedMatch.getJSONObject("info").getJSONArray("participants"), playersInfo);
                assert player != null;
                createTftImage(finishedMatch, player.getString("puuid"));

                System.out.println("Player info: " + player.toString());

                String fileName = "tftImage.png";
                EmbedCreateSpec finishedMatchMessageBuilder = EmbedCreateSpec.builder()
                        .color(getColoByPlacement(player.getInt("placement")))
                        .author("TFT Match Finished", "", "")
                        .title(player.getInt("placement") + "th place")
                        .addField(MessageBuilder.getTftMatchInformation(finishedMatch, player))
                        .image("attachment://" + fileName)
                        .build();

                editMessage(gameMessaged.getValue(), finishedMatchMessageBuilder, fileName);

                gamesAlreadyMessaged.remove(gameMessaged.getKey());
            }
        }

//        String fileName = "output.png";
//        EmbedCreateSpec finishedMatchMessageBuilder = EmbedCreateSpec.builder()
//                .color(Color.GREEN)
//                .author("TFT Match Finished", "", "")
//                .title("4th place")
////                .addField(MessageBuilder.getMatchInformation(finishedMatch))
//                .image("attachment://" + fileName)
//                .build();
//
//        editMessage(Snowflake.of("1343061569294307359"), finishedMatchMessageBuilder, fileName);
    }

    private void editMessage(Snowflake messageId, EmbedCreateSpec newContent, String fileName) {
        client.getChannelById(Snowflake.of(matchFindChannelId))
                .ofType(MessageChannel.class)
                .flatMap(channel -> channel.getMessageById(messageId))
                .flatMap(message -> {
                    try {
                        return message.edit(MessageEditSpec
                                .builder()
                                .addFile(fileName, new FileInputStream(fileName))
                                .embeds(Collections.singleton(newContent)).build());
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .block();
    }

    private void createTftImage(JSONObject finishedMatch, String participantToFind) {
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

    private JSONObject getPlayerToCreateImage(JSONArray participants, JSONArray playersInfo) {

        for (int i = 0; i < participants.toList().size(); i++) {
            System.out.println("Player that will be find to create image: " + participants.getJSONObject(i).getString("riotIdGameName"));
            String participantUuid = participants.getJSONObject(i).getString("puuid");

            for (int j = 0; j < playersInfo.toList().size(); j++) {
                String playerInfoUuid = playersInfo.getJSONObject(j).getString("puuid");

                if (participantUuid.equals(playerInfoUuid)) {
                    return participants.getJSONObject(i);
                }
            }
        }
        return null;
    }

    private JSONObject getRankingTftSolo(JSONArray tftRankings) {
        for (int i = 0; i < tftRankings.toList().size(); i++) {
            JSONObject ranking = tftRankings.getJSONObject(i);

            if (ranking.getString("queueType").equals("RANKED_TFT")) {
                return ranking;
            }
        }
        return null;
    }

    private Color getColoByPlacement(int placement) {
        return switch (placement) {
            case 1, 2, 3, 4 -> Color.GREEN;
            default -> Color.RED;
        };
    }
}
