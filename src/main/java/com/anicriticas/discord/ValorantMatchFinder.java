package com.anicriticas.discord;

import com.anicriticas.discord.messagebuilder.ValorantMessageBuilder;
import com.anicriticas.entities.valorant.Match;
import com.anicriticas.entities.valorant.Player;
import com.anicriticas.enums.valorant.MatchType;
import com.anicriticas.enums.valorant.Region;
import com.anicriticas.service.ValorantAPIService;
import com.anicriticas.utils.MatchUtils;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.*;

import static com.anicriticas.utils.FileUtils.getJsonArrayFromUrl;
import static com.anicriticas.utils.FileUtils.getResourceUrlByFileName;

@Component
public class ValorantMatchFinder {

    @Autowired
    GatewayDiscordClient client;

    private final ValorantAPIService valorantAPIService = new ValorantAPIService();

    private Map<Match, Snowflake> matchesAlreadyMessage = new HashMap<>();

    private Snowflake messageId = null;

    private final String matchFindChannelId = System.getenv("MATCH_FIND_CHANNEL_ID");
//    private final String matchFindChannelId = System.getenv("MATCH_FIND_CHANNEL_ID_PRD");

    // Deixar fixedRate parametrizável
//    @Scheduled(fixedRate = 180000)
////    @Scheduled(fixedRate = 30000)
//    public void MatchListener() {
//        JSONArray playersInfo;
//
////      TODO buscar buscar.json de players apenas uma vez
////        URL urlGeneralPlayersFile = getResourceUrlByFileName("match-find-players-valorant.json");
//        URL urlGeneralPlayersFile = getResourceUrlByFileName("match-find-players-valorant-test.json");
//        playersInfo = getJsonArrayFromUrl(urlGeneralPlayersFile);
//
//        for (int i = 0; i < playersInfo.toList().size(); i++) {
//            JSONObject playerInfo = playersInfo.getJSONObject(i);
//
//            Player player = new Player();
//            player.setPUUID(playerInfo.getString("puuid"));
//            player.setRegion(Region.getRegionByName(playerInfo.getString("region")));
//
//            Match lastMatch = valorantAPIService.getLastMatchIdByPlayerPuuid(player.getPUUID(), player.getRegion(), MatchType.COMPETITIVE);
//
//            boolean messageAlreadySend = false;
//
//            // If the match hasn't yet been notified on discord, then notify
//            for (Map.Entry<Match, Snowflake> matchAlreadyMessaged : matchesAlreadyMessage.entrySet()) {
//                if (Objects.equals(matchAlreadyMessaged.getKey().getId(), lastMatch.getId())) {
//                    messageAlreadySend = true;
//                    break;
//                }
//            }
//
//            if (messageAlreadySend) {
//                continue;
//            }
//
//            player = Player.getPlayerInList(player, lastMatch.getPlayers());
//
//            EmbedCreateSpec matchFoundMessageBuilder;
//
//            matchFoundMessageBuilder = EmbedCreateSpec.builder()
//                    .color(Objects.requireNonNull(MatchUtils.getValorantFinishedMatchColor(player, lastMatch)))
//                    .author("Match Finished", "", "")
//                    .title(lastMatch.getMatchType().getTypeName())
//                    .addField(ValorantMessageBuilder.getMatchInformation(lastMatch))
//                    .addField(ValorantMessageBuilder.getMatchPlayersKdaByTeam(lastMatch, "Blue"))
//                    .addField(ValorantMessageBuilder.getMatchPlayersKdaByTeam(lastMatch, "Red"))
//                    .build();
//
//            client.getChannelById(Snowflake.of(matchFindChannelId))
//                    .ofType(MessageChannel.class)
//                    .flatMap(channel -> channel.createMessage(matchFoundMessageBuilder)
//                            .doOnNext(message -> messageId = message.getId()))
//                    .block();
//
//            matchesAlreadyMessage.put(lastMatch, messageId);
//        }
//    }
}
