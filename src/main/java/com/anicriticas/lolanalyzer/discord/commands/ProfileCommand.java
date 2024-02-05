package com.anicriticas.lolanalyzer.discord.commands;

import com.anicriticas.lolanalyzer.discord.messagebuilder.MessageBuilder;
import com.anicriticas.lolanalyzer.discord.options.PlayerIdentifierOptions;
import com.anicriticas.lolanalyzer.enums.RegionEnum;
import com.anicriticas.lolanalyzer.service.LolAPIService;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.util.Objects;

import static com.anicriticas.lolanalyzer.utils.RiotAccountUtils.removeHashTagIfExists;

@RestController
public class ProfileCommand extends ListenerAdapter {

    private LolAPIService lolAPIService = new LolAPIService();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
//        event.deferReply().queue();

        if (event.getName().equals("profile")) {
            OptionMapping riotNickNameOption = event.getOption(PlayerIdentifierOptions.riotNickNameOption);
            OptionMapping riotIdOption = event.getOption(PlayerIdentifierOptions.riotIdOption);
            OptionMapping regionOption = event.getOption(PlayerIdentifierOptions.regionOption);

            if (Objects.isNull(riotNickNameOption) || Objects.isNull(riotIdOption) || Objects.isNull(regionOption)) {
                event.reply("profile command error").queue();
                return;
            }

            String riotNickName = riotNickNameOption.getAsString();
            String riotId = removeHashTagIfExists(riotIdOption.getAsString());

            RegionEnum region = RegionEnum.getByRegionName(regionOption.getAsString());

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

                MessageBuilder messageBuilder = new MessageBuilder();
                messageBuilder.embedBuilder.setColor(Color.CYAN);
                messageBuilder.embedBuilder.setAuthor("Profile");
                messageBuilder.setThumbnailWithProfileIcon(String.valueOf(summonerData.get("profileIconId")));
                messageBuilder.setProfileBasicInfo(summonerData, riotCompleteName, region, true);
                messageBuilder.setProfileTopChampions(topChampionsMastery, true);
                messageBuilder.setRankedStats(rankedStats, false);
                messageBuilder.setProfileRecentMatches(lastMatches, puuid, false);

                MessageEmbed embed = messageBuilder.embedBuilder.build();
                event.getHook().sendMessageEmbeds(embed).queue();
            } catch (Exception e) {
                event.getHook().sendMessage("Error when trying to retrieve Summoner profile: " + riotNickName).queue();
                throw new RuntimeException(e.getMessage());
            }
        }
    }
}
