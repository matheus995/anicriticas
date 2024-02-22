package com.anicriticas.lolanalyzer.discord;

import com.anicriticas.lolanalyzer.discord.messagebuilder.MessageBuilder;
import com.anicriticas.lolanalyzer.enums.RegionEnum;
import com.anicriticas.lolanalyzer.service.LolAPIService;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static com.anicriticas.lolanalyzer.utils.JsonUtils.getJsonArrayFromUrl;

@Component
public class MatchFinder {

    @Autowired
    GatewayDiscordClient client;

    private final LolAPIService lolAPIService = new LolAPIService();

    Map<String, JSONObject> playersInGame = new HashMap<>();
    List<Long> gameIdsAlreadyMessaged = new ArrayList<>();

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

        // Check if have anyone in game
        for (int i = 0; i < playersInfo.toList().size(); i++) {
            JSONObject playerInfo = playersInfo.getJSONObject(i);

            String puuid = playerInfo.getString("puuid");
            String summonerId = playerInfo.getString("summonerId");
            RegionEnum accountRegion = RegionEnum.getByRegionName(playerInfo.getString("region"));

            JSONObject activeGame = lolAPIService.getActiveGamesBySummonerId(summonerId, accountRegion);

            if (Objects.isNull(activeGame)) {
                playersInGame.remove(puuid);
                continue;
            }

            if (playersInGame.containsKey(puuid)) {
                if (activeGame.getLong("gameId") != playersInGame.get(puuid).getLong("gameId")) {
                    playersInGame.replace(puuid, activeGame);
                }
                continue;
            }

            playersInGame.put(playerInfo.getString("puuid"), activeGame);
        }

        //TODO da forma que está as listas vão crescer infinitamente, descobrir uma forma de ir limpando as listas
        //TODO se for uma partida personalizada ou training mode não notificar
        for (Map.Entry<String, JSONObject> gameInfo : playersInGame.entrySet()) {
            if (!gameIdsAlreadyMessaged.contains(gameInfo.getValue().getLong("gameId"))) {
                gameIdsAlreadyMessaged.add(gameInfo.getValue().getLong("gameId"));

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
                        .flatMap(channel -> channel.createMessage(matchFoundMessageBuilder))
                        .subscribe();
            }
        }
    }
}
