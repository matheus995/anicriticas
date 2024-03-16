package com.anicriticas.lolanalyzer.discord.commands;

import com.anicriticas.lolanalyzer.discord.messagebuilder.MessageBuilder;
import com.anicriticas.lolanalyzer.discord.options.PlayerIdentifierOptions;
import com.anicriticas.lolanalyzer.enums.RegionEnum;
import com.anicriticas.lolanalyzer.service.LolAPIService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Color;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.anicriticas.lolanalyzer.utils.RiotAccountUtils.removeHashTagIfExists;

@Slf4j
@Component
public class ProfileCommand implements ISlashCommand {

    @Autowired
    private LolAPIService lolAPIService;

    private static final String commandName = "profile";

    public static ApplicationCommandRequest profileCommandRequest() {

        return ApplicationCommandRequest.builder()
                .name(commandName)
                .description("Retrieve profile data from a summoner")
                .addAllOptions(PlayerIdentifierOptions.getPlayerIdentifierOptions())
                .build();
    }

    @Override
    public String getName() {
        return commandName;
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        String riotNickName = event.getOption(PlayerIdentifierOptions.riotNickNameOption)
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get();

        String riotId = removeHashTagIfExists(event.getOption(PlayerIdentifierOptions.riotIdOption)
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get());

        RegionEnum region = RegionEnum.getByRegionName(event.getOption(PlayerIdentifierOptions.regionOption)
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get());

        try {
            JSONObject riotAccount = new JSONObject(lolAPIService.getRiotAccountByNameAndId(riotNickName, riotId, region));
            String puuid = riotAccount.getString("puuid");
            riotNickName = riotAccount.getString("gameName");
            riotId = riotAccount.getString("tagLine");

            String riotCompleteName = riotNickName + " #" + riotId;

            JSONObject summonerData = new JSONObject(lolAPIService.getSummonerByPuuid(puuid, region));
            String summonerId = summonerData.getString("id");

            String[] lastMatchesId = lolAPIService.getLastMatchesIdsBySummonerPuuid(puuid, "3", region);
            JSONArray lastMatches = new JSONArray();

            for (int i = 0; i < lastMatchesId.length; i++) {
                lastMatches.put(lolAPIService.getMatchById(lastMatchesId[i], region));
            }

            JSONArray topChampionsMastery = lolAPIService.getTopChampionsMasteryBySummonerPuuid(puuid, region);
            JSONArray rankedStats = lolAPIService.getRankedStats(summonerId, region);

            EmbedCreateSpec lastMatchMessageBuilder = EmbedCreateSpec.builder()
                    .color(Color.CYAN)
                    .author("Profile", "", "")
                    .thumbnail(MessageBuilder.getThumbnailWithProfileIcon(String.valueOf(summonerData.get("profileIconId"))))
                    .addField(MessageBuilder.getProfileBasicInfo(summonerData, riotCompleteName, region, true))
                    .addField(MessageBuilder.getProfileTopChampions(topChampionsMastery, true))
                    .addField(MessageBuilder.getRankedStats(rankedStats, false))
                    .addField(MessageBuilder.getProfileRecentMatches(lastMatches, puuid, false))
                    .build();

            return event.createFollowup().withEmbeds(lastMatchMessageBuilder).then();
        } catch (Exception e) {
            log.error(e.getMessage());
            return event.createFollowup("Error when trying to retrieve Summoner profile: " + riotNickName).then();
        }
    }
}
