package com.anicriticas.lolanalyzer.discord;

import com.anicriticas.lolanalyzer.discord.messagebuilder.MessageBuilder;
import com.anicriticas.lolanalyzer.enums.RegionEnum;
import com.anicriticas.lolanalyzer.service.LolAPIService;
import com.anicriticas.lolanalyzer.utils.MatchUtils;
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

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.anicriticas.lolanalyzer.utils.JsonUtils.getJsonArrayFromUrl;

@Component
public class MatchFinder {

    @Autowired
    GatewayDiscordClient client;

    private final LolAPIService lolAPIService = new LolAPIService();

    private Map<String, JSONObject> playersInGame = new HashMap<>();

    private Map<JSONObject, Snowflake> gamesAlreadyMessaged = new HashMap<>();

    private Snowflake messageId = null;

    private String playerSide = "";

    // Deixar fixedRate parametrizável
    @Scheduled(fixedRate = 180000)
    public void MatchListener() {
        JSONArray playersInfo;

        //TODO buscar buscar.json de players apenas uma vez
        try {
            URL urlGeneralEmojiFile = MatchFinder.class.getClassLoader().getResource("match-find-players.json");
            playersInfo = getJsonArrayFromUrl(urlGeneralEmojiFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < playersInfo.toList().size(); i++) {
            JSONObject playerInfo = playersInfo.getJSONObject(i);

            String puuid = playerInfo.getString("puuid");
            String summonerId = playerInfo.getString("summonerId");
            RegionEnum accountRegion = RegionEnum.getByRegionName(playerInfo.getString("region"));

            JSONObject activeGame = lolAPIService.getActiveGamesBySummonerId(summonerId, accountRegion);

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

        //TODO se for uma partida personalizada ou training mode, não notificar
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

            EmbedCreateSpec matchFoundMessageBuilder = EmbedCreateSpec.builder()
                    .color(Color.CYAN)
                    .author("Match Found", "", "")
                    .addField(MessageBuilder.getMatchFoundInformation(gameInfo.getValue()))
                    .addField(MessageBuilder.getMatchFoundBans(gameInfo.getValue()))
                    .addField(MessageBuilder.getMatchFoundBlueSidePlayers(gameInfo.getValue().getJSONArray("participants"), true))
                    .addField(MessageBuilder.getMatchFoundRedSidePlayers(gameInfo.getValue().getJSONArray("participants"), true))
                    .build();

            client.getChannelById(Snowflake.of(System.getenv("MATCH_FIND_CHANNEL_ID")))
                    .ofType(MessageChannel.class)
                    .flatMap(channel -> channel.createMessage(matchFoundMessageBuilder)
                            .doOnNext(message -> messageId = message.getId()))
                    .block();

            gamesAlreadyMessaged.put(gameInfo.getValue(), messageId);
        }

        // If match finished, edit the original message with the result of the match
        for (Map.Entry<JSONObject, Snowflake> gameMessaged : gamesAlreadyMessaged.entrySet()) {

            RegionEnum matchRegion = RegionEnum.getRegionByEnumName(gameMessaged.getKey().getString("platformId"));
            String matchId = matchRegion.name() + "_" + gameMessaged.getKey().get("gameId");

            // Search if match it's already finished
            JSONObject finishedMatch = lolAPIService.getFinishedMatchById(matchId, matchRegion);

            // If match finished, edit original message
            if (Objects.nonNull(finishedMatch)) {
                // Set playerSide - BLUE or RED
                JSONArray participants = MatchUtils.getMatchParticipants(finishedMatch);

                participants.forEach(participant -> {
                    JSONObject jsonObject = (JSONObject) participant;
                    playersInfo.forEach(playerInfo -> {
                        JSONObject player = (JSONObject) playerInfo;
                        if (player.getString("puuid").equals(jsonObject.getString("puuid"))) {
                            playerSide = MatchUtils.getTeamSide(jsonObject.getInt("teamId"));
                        }
                    });
                });

                String matchResult = MatchUtils.getFinishedMatchResult(playerSide, finishedMatch);

                EmbedCreateSpec finishedMatchMessageBuilder = EmbedCreateSpec.builder()
                        .color(Objects.requireNonNull(MatchUtils.getFinishedMatchColor(playerSide, finishedMatch)))
                        .author("Match Finished", "", "")
                        .title(MessageBuilder.getFinishedMatchResult(matchResult, finishedMatch))
                        .addField(MessageBuilder.getMatchInformation(finishedMatch))
                        .addField(Objects.requireNonNull(MessageBuilder.getMatchBans(finishedMatch)))
                        .addField(MessageBuilder.getMatchPlayersKdaBlueTeam(finishedMatch))
                        .addField(MessageBuilder.getMatchPlayersKdaRedTeam(finishedMatch))
                        .build();

                editMessage(gameMessaged.getValue(), finishedMatchMessageBuilder);

                gamesAlreadyMessaged.remove(gameMessaged.getKey());
            }
        }
    }

    private void editMessage(Snowflake messageId, EmbedCreateSpec newContent) {
        client.getChannelById(Snowflake.of(System.getenv("MATCH_FIND_CHANNEL_ID")))
                .ofType(MessageChannel.class)
                .flatMap(channel -> channel.getMessageById(messageId))
                .flatMap(message -> message.edit(MessageEditSpec.builder().embeds(Collections.singleton(newContent)).build()))
                .block();
    }
}
