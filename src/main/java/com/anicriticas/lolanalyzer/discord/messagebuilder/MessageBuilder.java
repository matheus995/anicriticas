package com.anicriticas.lolanalyzer.discord.messagebuilder;

import com.anicriticas.lolanalyzer.ChampionId;
import com.anicriticas.lolanalyzer.enums.RegionEnum;
import com.anicriticas.lolanalyzer.utils.MatchUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.Objects;

import static com.anicriticas.lolanalyzer.ChampionsEmojis.getEmojiByChampionName;
import static com.anicriticas.lolanalyzer.ElosEmojis.getEmojiByElo;
import static com.anicriticas.lolanalyzer.GeneralEmojis.getEmojiByName;
import static com.anicriticas.lolanalyzer.utils.MatchUtils.getMatchResult;

public class MessageBuilder {

    public EmbedBuilder embedBuilder = new EmbedBuilder();

    public void setThumbnailWithProfileIcon(String profileIconId) {
        embedBuilder.setThumbnail("https://ddragon.leagueoflegends.com/cdn/14.2.1/img/profileicon/" + profileIconId + ".png");
    }

    public void setProfileBasicInfo(JSONObject summonerProfile, String riotNickName, RegionEnum region, boolean inline) {
        String[] profileInfo = {String.format("`Name:` %s", riotNickName), String.format("`Level:` %s", summonerProfile.get("summonerLevel")), String.format("`Region:` %s", region.getRegionName())};
        embedBuilder.addField("> Basic information", String.join("\n", profileInfo), inline);
    }

    public void setProfileTopChampions(JSONArray topChampionsMastery, boolean inline) {
        if (Objects.isNull(topChampionsMastery)) {
            embedBuilder.addField("> Top 3 Champions", "This summoner has not played champions", inline);
            return;
        }

        StringBuilder topChampions = new StringBuilder();

        for (int i = 0; i < topChampionsMastery.toList().size(); i++) {
            JSONObject champion = topChampionsMastery.getJSONObject(i);

            String championName = ChampionId.getChampionById(String.valueOf(champion.get("championId")));
            int level = champion.getInt("championLevel");
            long points = champion.getLong("championPoints");

            topChampions.append(String.format("`%d.` %s %s (Level %d, **%s**)\n", i + 1, getEmojiByChampionName(championName), championName, level, MatchUtils.humanReadableInt(points)));
        }

        embedBuilder.addField(String.format("> Top %s Champions", topChampionsMastery.toList().size()), topChampions.toString(), true);
    }

    public void setRankedStats(JSONArray rankedStats, boolean inline) {
        if (Objects.isNull(rankedStats)) {
            embedBuilder.addField("> Ranked Stats", "This summoner has not played ranked games", inline);
            return;
        }

        String soloQText = "*Unranked*";
        String flexSRText = "*Unranked*";
        String tftText = "*Unranked*";

        for (int i = 0; i < rankedStats.toList().size(); i++) {
            JSONObject ranked = rankedStats.getJSONObject(i);

            switch (ranked.getString("queueType")) {
                case "RANKED_SOLO_5x5":
                    soloQText = String.format("%s %s %s (**%s LP**) (**%s W** / **%s L**, %s",
                            getEmojiByElo(ranked.getString("tier")),
                            ranked.getString("tier"),
                            ranked.getString("rank"),
                            ranked.getInt("leaguePoints"),
                            ranked.getInt("wins"),
                            ranked.getInt("losses"),
                            Math.round((ranked.getInt("wins") * 100d) / (ranked.getInt("wins") + ranked.getInt("losses"))) + "%)");
                    break;
                case "RANKED_FLEX_SR":
                    flexSRText = String.format("%s %s %s (**%s LP**) (**%s W** / **%s L**, %s",
                            getEmojiByElo(ranked.getString("tier")),
                            ranked.getString("tier"),
                            ranked.getString("rank"),
                            ranked.getInt("leaguePoints"),
                            ranked.getInt("wins"),
                            ranked.getInt("losses"),
                            Math.round((ranked.getInt("wins") * 100d) / (ranked.getInt("wins") + ranked.getInt("losses"))) + "%)");
                    break;
                case "RANKED_TFT":
                    tftText = String.format("%s %s %s (**%s LP**) (**%s W** / **%s L**, %s",
                            getEmojiByElo(ranked.getString("tier")),
                            ranked.getString("tier"),
                            ranked.getString("rank"),
                            ranked.getInt("leaguePoints"),
                            ranked.getInt("wins"),
                            ranked.getInt("losses"),
                            Math.round((ranked.getInt("wins") * 100d) / (ranked.getInt("wins") + ranked.getInt("losses"))) + "%)");
                    break;
            }
        }

        String[] summonerRankedStatsText = {String.format("`Solo/Duo:` %s", soloQText), String.format("`Flex SR:` %s", flexSRText), String.format("`TFT:` %s", tftText)};
        embedBuilder.addField("> Ranked Stats", String.join("\n", summonerRankedStatsText), false);
    }

    public void setProfileRecentMatches(JSONArray lastMatches, String puuid, boolean inline) {
        if (Objects.isNull(lastMatches)) {
            embedBuilder.addField("> Recent Matches", "This summoner has not played in a while", inline);
            return;
        }

        StringBuilder lastMatchesText = new StringBuilder();

        for (int i = 0; i < lastMatches.toList().size(); i++) {
            JSONObject match = lastMatches.getJSONObject(i);

            JSONObject participant = MatchUtils.getParticipantBySummonerPuuid(puuid, match);
            String championName = ChampionId.getChampionById(String.valueOf(participant.get("championId")));

            lastMatchesText.append(String.format("`%d.` %s %s %s  (%s)\n",
                    i + 1,
                    getEmojiByName(getMatchResult(participant)),
                    getEmojiByChampionName(championName),
                    championName,
                    (MatchUtils.getMatchDate(match.getJSONObject("info").getLong("gameStartTimestamp")))));
        }

        embedBuilder.addField("> Recent Matches", lastMatchesText.toString(), inline);
    }

    public void setMatchResult(String summonerName, JSONObject participant, JSONObject lastMatch) {
        embedBuilder.setTitle(
                summonerName + " - " +
                        MatchUtils.getMatchResult(participant) + " - " +
                        MatchUtils.getGameType(lastMatch.getJSONObject("info").getInt("queueId")).getQueueDescription()
        );
    }

    public void setMatchInformation(JSONObject lastMatch) {
        String matchDate = MatchUtils.getMatchDate(lastMatch.getJSONObject("info").getLong("gameStartTimestamp"));
        String matchDuration = MatchUtils.getMatchDuration(lastMatch.getJSONObject("info").getLong("gameStartTimestamp"), lastMatch.getJSONObject("info").getLong("gameEndTimestamp"));
        String[] matchInformation = {String.format("`Date:` %s", matchDate), String.format("`Duration:` %s", matchDuration)};
        embedBuilder.addField("> Match information", String.join("\n", matchInformation), false);
    }

    public void setMatchBans(JSONObject lastMatch) {
        if (MatchUtils.getMatchBlueSideBans(lastMatch).isEmpty()) {
            return;
        }

        String[] bannedChampions = {String.format("`Blue team:` %s", String.join(" ", MatchUtils.getMatchBlueSideBansWithEmojis(lastMatch))), String.format("`Red team:` %s", String.join(" ", MatchUtils.getMatchRedSideBansWithEmojis(lastMatch))),};
        embedBuilder.addField("> Banned champions", String.join("\n", bannedChampions), false);
    }

    public void setMatchPlayersKda(JSONObject lastMatch) {
        JSONArray participants = MatchUtils.getMatchParticipants(lastMatch);

        StringBuilder blueTeamParticipants = new StringBuilder();
        StringBuilder redTeamParticipants = new StringBuilder();

        for (int i = 0; i < participants.toList().size(); i++) {
            JSONObject participant = participants.getJSONObject(i);

            String summonerName = participant.getString("riotIdGameName");
            String championName = ChampionId.getChampionById(String.valueOf(participant.get("championId")));

            int summonerKills = participant.getInt("kills");
            int summonerDeaths = participant.getInt("deaths");
            int summonerAssists = participant.getInt("assists");
            int totalCS = participant.getInt("totalMinionsKilled") + participant.getInt("neutralMinionsKilled");

            String formattedTotalSummonerStats = String.format("(**%s**/**%s**/**%s** **%s CS**)", summonerKills, summonerDeaths, summonerAssists, totalCS);

            if (MatchUtils.isBlueSide(participant.getInt("teamId"))) {
                blueTeamParticipants.append(formatPlayerKdaToFitInOneLine(getEmojiByChampionName(championName), summonerName, formattedTotalSummonerStats));
            } else {
                redTeamParticipants.append(formatPlayerKdaToFitInOneLine(getEmojiByChampionName(championName), summonerName, formattedTotalSummonerStats));
            }
        }

        embedBuilder.addField("> Blue team", String.join("", blueTeamParticipants), true);
        embedBuilder.addField("> Red team", String.join("", redTeamParticipants), true);
    }

    private String formatPlayerKdaToFitInOneLine(String championEmoji, String summonerName, String playerStats) {
        String stringWithoutEmoji = String.format("%s | %s\n", summonerName, playerStats);

        if (stringWithoutEmoji.length() >= 45) {
            summonerName = summonerName.substring(0, 6) + ".";
        }

        return String.format("%s %s | %s\n", championEmoji, summonerName, playerStats);
    }
}
