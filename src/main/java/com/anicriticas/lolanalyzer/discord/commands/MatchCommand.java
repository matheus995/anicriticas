package com.anicriticas.lolanalyzer.discord.commands;

import com.anicriticas.lolanalyzer.discord.messagebuilder.MessageBuilder;
import com.anicriticas.lolanalyzer.discord.options.PlayerIdentifierOptions;
import com.anicriticas.lolanalyzer.enums.RegionEnum;
import com.anicriticas.lolanalyzer.service.LolAPIService;
import com.anicriticas.lolanalyzer.utils.MatchUtils;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Color;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.anicriticas.lolanalyzer.utils.RiotAccountUtils.removeHashTagIfExists;

@Slf4j
@Component
public class MatchCommand implements ISlashCommand {

    @Autowired
    private LolAPIService lolAPIService;

    private static final String commandName = "lastmatch";

    public static ApplicationCommandRequest lastMatchCommandRequest() {

        return ApplicationCommandRequest.builder()
                .name(commandName)
                .description("Retrieve last match info from a Summoner")
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

//            TODO logica para verificar se a partida é uma partida válida, por exemplo, se for modo treino buscar a próxima
            String lastMatchId = lolAPIService.getLastMatchesIdsBySummonerPuuid(puuid, "1", region)[0];
            JSONObject lastMatch = lolAPIService.getMatchById(lastMatchId, region);

            JSONObject participant = MatchUtils.getParticipantBySummonerPuuid(puuid, lastMatch);
            assert participant != null;

            EmbedCreateSpec lastMatchMessageBuilder = EmbedCreateSpec.builder()
                    .color(Color.CYAN)
                    .author("Last Match", "", "")
                    .thumbnail(MessageBuilder.getThumbnailWithProfileIcon(String.valueOf(summonerData.get("profileIconId"))))
                    .title(MessageBuilder.getMatchResult(riotCompleteName, participant, lastMatch))
                    .addField(MessageBuilder.getMatchInformation(lastMatch))
                    .addField(MessageBuilder.getMatchBans(lastMatch))
                    .addField(MessageBuilder.getMatchPlayersKdaBlueTeam(lastMatch))
                    .addField(MessageBuilder.getMatchPlayersKdaRedTeam(lastMatch))
                    .build();

            return event.createFollowup().withEmbeds(lastMatchMessageBuilder).then();
        } catch (Exception e) {
            log.error(e.getMessage());
            return event.createFollowup("Error when trying to retrieve last match from Summoner: " + riotNickName).then();
        }
    }
}
