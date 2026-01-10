package com.anicriticas.discord;

import com.anicriticas.discord.messagebuilder.ValorantMessageBuilder;
import com.anicriticas.entities.valorant.Match;
import com.anicriticas.enums.valorant.MatchType;
import com.anicriticas.enums.valorant.Region;
import com.anicriticas.model.NotifiedMatches;
import com.anicriticas.model.Players;
import com.anicriticas.repository.NotifiedMatchesRepository;
import com.anicriticas.repository.PlayersRepository;
import com.anicriticas.service.ValorantAPIService;
import com.anicriticas.utils.MatchUtils;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ValorantMatchFinderWithDB {

    @Autowired
    GatewayDiscordClient client;

    @Autowired
    PlayersRepository playersRepository;

    @Autowired
    NotifiedMatchesRepository notifiedMatchesRepository;

    private final ValorantAPIService valorantAPIService = new ValorantAPIService();

    private Snowflake messageId = null;

    private final String matchFindChannelId = System.getenv("MATCH_FIND_CHANNEL_ID");
//    private final String matchFindChannelId = System.getenv("MATCH_FIND_CHANNEL_ID_PRD");

    // Deixar fixedRate parametrizável
    @Scheduled(fixedRate = 180000)
    public void MatchListener() {
        List<Players> playersList = playersRepository.findAll();

        for (Players player : playersList) {
            Match lastMatch = valorantAPIService.getLastMatchIdByPlayerPuuid(player.getPuuid(), Region.getRegionByName(player.getRegion().getName()), MatchType.COMPETITIVE);

            Optional<NotifiedMatches> notifiedMatch = notifiedMatchesRepository.findById(UUID.fromString(lastMatch.getId()));

            // If the match its already notified or match date is out of range than go to the next player
            if (notifiedMatch.isPresent() || !MatchUtils.isDateInRange(lastMatch.getStartDate(), 7L)) {
                continue;
            }

            // If the match hasn't yet been notified on discord, then save in DB and notify
            player = Players.getPlayerInList(player, lastMatch.getPlayers());

            EmbedCreateSpec matchFoundMessageBuilder;

            String matchResult = MatchUtils.getValorantFinishedMatchResult(player, lastMatch);

            matchFoundMessageBuilder = EmbedCreateSpec.builder()
                    .color(Objects.requireNonNull(MatchUtils.getValorantFinishedMatchColor(player, lastMatch)))
                    .title(lastMatch.getMatchType().getTypeName() + " - " + matchResult)
                    .addField(ValorantMessageBuilder.getMatchInformation(lastMatch, matchResult))
                    .addField(ValorantMessageBuilder.getMatchPlayersKdaByTeam(lastMatch, "Blue"))
                    .addField(ValorantMessageBuilder.getMatchPlayersKdaByTeam(lastMatch, "Red"))
                    .build();

            notifiedMatchesRepository.save(new NotifiedMatches(UUID.fromString(lastMatch.getId()), lastMatch.getStartDate(), lastMatch.getMap()));

            client.getChannelById(Snowflake.of(matchFindChannelId))
                    .ofType(MessageChannel.class)
                    .flatMap(channel -> channel.createMessage(matchFoundMessageBuilder)
                            .doOnNext(message -> messageId = message.getId()))
                    .block();
        }
    }
}
