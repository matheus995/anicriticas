package com.anicriticas.discord.commands;

import com.anicriticas.discord.messagebuilder.MessageBuilder;
import com.anicriticas.discord.options.PlayerIdentifierOptions;
import com.anicriticas.enums.Region;
import com.anicriticas.service.LolAPIService;
import com.anicriticas.service.RiotAPIService;
import com.anicriticas.utils.MatchUtils;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Color;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.anicriticas.utils.RiotAccountUtils.removeHashTagIfExists;

@Slf4j
@Component
public class MatchCommand implements ISlashCommand {

    private final RiotAPIService riotAPIService = new RiotAPIService();
    private final LolAPIService lolAPIService = new LolAPIService();

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

        Region region = Region.getByRegionName(event.getOption(PlayerIdentifierOptions.regionOption)
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get());

        try {
            JSONObject riotAccount = new JSONObject(riotAPIService.getRiotAccountByNameAndId(riotNickName, riotId, region));
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
