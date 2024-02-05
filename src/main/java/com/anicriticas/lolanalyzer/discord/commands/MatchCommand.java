package com.anicriticas.lolanalyzer.discord.commands;

import com.anicriticas.lolanalyzer.discord.messagebuilder.MessageBuilder;
import com.anicriticas.lolanalyzer.discord.options.PlayerIdentifierOptions;
import com.anicriticas.lolanalyzer.enums.RegionEnum;
import com.anicriticas.lolanalyzer.service.LolAPIService;
import com.anicriticas.lolanalyzer.utils.MatchUtils;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.util.Objects;

@RestController
public class MatchCommand extends ListenerAdapter {

    private LolAPIService lolAPIService = new LolAPIService();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        if (event.getName().equals("lastmatch")) {
            OptionMapping riotNickNameOption = event.getOption(PlayerIdentifierOptions.riotNickNameOption);
            OptionMapping riotIdOption = event.getOption(PlayerIdentifierOptions.riotIdOption);
            OptionMapping regionOption = event.getOption(PlayerIdentifierOptions.regionOption);

            if (Objects.isNull(riotNickNameOption) || Objects.isNull(riotIdOption) || Objects.isNull(regionOption)) {
                event.reply("lastmatch command error").queue();
                return;
            }

            String riotNickName = riotNickNameOption.getAsString();
            String riotId = riotIdOption.getAsString();
            String riotCompleteName = riotNickName + " #" + riotId;
            RegionEnum region = RegionEnum.getByRegionName(regionOption.getAsString());

            try {
                JSONObject riotAccount = new JSONObject(lolAPIService.getRiotAccountByNameAndId(riotNickName, riotId, region));
                String puuid = riotAccount.getString("puuid");

                JSONObject summonerData = new JSONObject(lolAPIService.getSummonerByPuuid(puuid, region));

                String lastMatchId = lolAPIService.getLastMatchIdBySummonerPuuid(puuid, region);
                JSONObject lastMatch = lolAPIService.getMatchById(lastMatchId, region);

                JSONObject participant = MatchUtils.getParticipantBySummonerPuuid(puuid, lastMatch);
                assert participant != null;

                MessageBuilder messageBuilder = new MessageBuilder();
                messageBuilder.embedBuilder.setColor(Color.CYAN);
                messageBuilder.embedBuilder.setAuthor("Last Match");
                messageBuilder.setThumbnailWithProfileIcon(String.valueOf(summonerData.get("profileIconId")));
                messageBuilder.setMatchResult(riotCompleteName, participant, lastMatch);
                messageBuilder.setMatchInformation(lastMatch);
                messageBuilder.setMatchBans(lastMatch);
                messageBuilder.setMatchPlayersKda(lastMatch);

                MessageEmbed embed = messageBuilder.embedBuilder.build();
                event.getHook().sendMessageEmbeds(embed).queue();
            } catch (Exception e) {
                event.getHook().sendMessage("Error when trying to retrieve last match from Summoner: " + riotNickName).queue();
                throw new RuntimeException(e.getMessage());
            }
        }
    }
}
