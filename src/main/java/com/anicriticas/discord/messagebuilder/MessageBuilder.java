package com.anicriticas.discord.messagebuilder;

import com.anicriticas.enums.QueueEnum;
import com.anicriticas.enums.Region;
import com.anicriticas.utils.ChampionUtils;
import com.anicriticas.utils.MatchUtils;
import discord4j.core.spec.EmbedCreateFields;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import static com.anicriticas.emojis.ChampionsEmojis.getEmojiByChampionName;
import static com.anicriticas.emojis.ElosEmojis.getEmojiByElo;
import static com.anicriticas.emojis.GeneralEmojis.getEmojiByName;

public class MessageBuilder {

    public static String getThumbnailWithProfileIcon(String profileIconId) {
        // TODO verificar versao do ddragon: https://ddragon.leagueoflegends.com/api/versions.json
        return "https://ddragon.leagueoflegends.com/cdn/15.3.1/img/profileicon/" + profileIconId + ".png";
    }

    public static EmbedCreateFields.Field getProfileBasicInfo(JSONObject summonerProfile, String riotNickName, Region region, boolean inline) {
        String[] profileInfo = {String.format("`Name:` %s", riotNickName), String.format("`Level:` %s", summonerProfile.get("summonerLevel")), String.format("`Region:` %s", region.getRegionName())};

        return EmbedCreateFields.Field.of("> Basic information", String.join("\n", profileInfo), inline);
    }

    public static EmbedCreateFields.Field getProfileTopChampions(JSONArray topChampionsMastery, boolean inline) {

        if (Objects.isNull(topChampionsMastery)) {
            return EmbedCreateFields.Field.of("> Top 3 Champions", "This summoner has not played with any champions", inline);
        }

        StringBuilder topChampions = new StringBuilder();

        for (int i = 0; i < topChampionsMastery.toList().size(); i++) {
            JSONObject champion = topChampionsMastery.getJSONObject(i);

            String championName = ChampionUtils.getChampionById(String.valueOf(champion.get("championId")));
            int level = champion.getInt("championLevel");
            long points = champion.getLong("championPoints");

            topChampions.append(String.format("`%d.` %s %s (Level %d, **%s**)\n", i + 1, getEmojiByChampionName(championName), championName, level, MatchUtils.humanReadableInt(points)));
        }

        return EmbedCreateFields.Field.of(String.format("> Top %s Champions", topChampionsMastery.toList().size()), topChampions.toString(), true);
    }

    public static EmbedCreateFields.Field getRankedStats(JSONArray rankedStats, boolean inline) {

        if (Objects.isNull(rankedStats)) {
            return EmbedCreateFields.Field.of("> Ranked Stats", "This summoner has not played ranked games", inline);
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
//                case "RANKED_TFT":
//                    tftText = String.format("%s %s %s (**%s LP**) (**%s W** / **%s L**, %s",
//                            getEmojiByElo(ranked.getString("tier")),
//                            ranked.getString("tier"),
//                            ranked.getString("rank"),
//                            ranked.getInt("leaguePoints"),
//                            ranked.getInt("wins"),
//                            ranked.getInt("losses"),
//                            Math.round((ranked.getInt("wins") * 100d) / (ranked.getInt("wins") + ranked.getInt("losses"))) + "%)");
//                    break;
            }
        }

//        String[] summonerRankedStatsText = {String.format("`Solo/Duo:` %s", soloQText), String.format("`Flex SR:` %s", flexSRText), String.format("`TFT:` %s", tftText)};
        String[] summonerRankedStatsText = {String.format("`Solo/Duo:` %s", soloQText), String.format("`Flex SR:` %s", flexSRText)};

        return EmbedCreateFields.Field.of("> Ranked Stats", String.join("\n", summonerRankedStatsText), inline);
    }

    public static EmbedCreateFields.Field getProfileRecentMatches(JSONArray lastMatches, String puuid, boolean inline) {

        if (Objects.isNull(lastMatches)) {
            return EmbedCreateFields.Field.of("> Recent Matches", "This summoner has not played in a while", inline);
        }

        StringBuilder lastMatchesText = new StringBuilder();

        for (int i = 0; i < lastMatches.toList().size(); i++) {
            JSONObject match = lastMatches.getJSONObject(i);

            JSONObject participant = MatchUtils.getParticipantBySummonerPuuid(puuid, match);
            String championName = ChampionUtils.getChampionById(String.valueOf(participant.get("championId")));

            lastMatchesText.append(String.format("`%d.` %s %s %s  (%s)\n",
                    i + 1,
                    getEmojiByName(MatchUtils.getMatchResult(participant)),
                    getEmojiByChampionName(championName),
                    championName,
                    (MatchUtils.getMatchDate(match.getJSONObject("info").getLong("gameStartTimestamp")))));
        }

        return EmbedCreateFields.Field.of("> Recent Matches", lastMatchesText.toString(), inline);
    }

    public static String getMatchResult(String summonerName, JSONObject participant, JSONObject lastMatch) {
        return summonerName + " - " +
                MatchUtils.getMatchResult(participant) + " - " +
                MatchUtils.getGameType(lastMatch.getJSONObject("info").getInt("queueId")).getQueueDescription();
    }

    public static String getFinishedMatchResult(String matchResult, JSONObject lastMatch) {
        return matchResult + " - " + MatchUtils.getGameType(lastMatch.getJSONObject("info").getInt("queueId")).getQueueDescription();
    }

    public static EmbedCreateFields.Field getMatchInformation(JSONObject lastMatch) {
        String matchDate = MatchUtils.getMatchDate(lastMatch.getJSONObject("info").getLong("gameStartTimestamp"));
        String matchDuration = MatchUtils.getMatchDuration(lastMatch.getJSONObject("info").getLong("gameStartTimestamp"), lastMatch.getJSONObject("info").getLong("gameEndTimestamp"));
        String[] matchInformation = {String.format("`Date:` %s", matchDate), String.format("`Duration:` %s", matchDuration)};

        return EmbedCreateFields.Field.of("> Match information", String.join("\n", matchInformation), false);
    }

    public static EmbedCreateFields.Field getTftMatchInformation(JSONObject finishedMatch, JSONObject player) {
        String matchDate = MatchUtils.getMatchDate(finishedMatch.getJSONObject("info").getLong("game_datetime"));
        String[] matchInformation = {String.format("`Date:` %s", matchDate), String.format("`Player:` %s", player.get("riotIdGameName") + "#" + player.get("riotIdTagline"))};

        return EmbedCreateFields.Field.of("> Match information", String.join("\n", matchInformation), false);
    }

    public static EmbedCreateFields.Field getMatchFoundInformation(JSONObject matchFound) {
        String matchDate = MatchUtils.getMatchDate(matchFound.getLong("gameStartTime"));
        QueueEnum queueType = MatchUtils.getGameType(matchFound.getInt("gameQueueConfigId"));
        String[] matchInformation = {String.format("`Date:` %s", matchDate), String.format("`Game Type:` %s", queueType.getQueueDescription())};

        return EmbedCreateFields.Field.of("> Match information", String.join("\n", matchInformation), false);
    }

    public static EmbedCreateFields.Field getTftMatchFoundInformation() {
        String matchDate = MatchUtils.getMatchDate(1739447096L);
        String[] matchInformation = {String.format("`Date:` %s", matchDate), String.format("`Game Type:` %s", QueueEnum.ARAM.getQueueDescription())};

        return EmbedCreateFields.Field.of("> Match information", String.join("\n", matchInformation), false);
    }

    public static EmbedCreateFields.Field getMatchBans(JSONObject lastMatch) {
        if (MatchUtils.getMatchBlueSideBans(lastMatch).isEmpty()) {
            return EmbedCreateFields.Field.of("", "", false);
        }

        String[] bannedChampions = {String.format("`Blue team:` %s", String.join(" ", MatchUtils.getMatchBlueSideBansWithEmojis(lastMatch))), String.format("`Red team:` %s", String.join(" ", MatchUtils.getMatchRedSideBansWithEmojis(lastMatch))),};

        return EmbedCreateFields.Field.of("> Banned champions", String.join("\n", bannedChampions), false);
    }

    public static EmbedCreateFields.Field getMatchFoundBans(JSONObject matchFound) {
        if (MatchUtils.getMatchFoundBlueSideBansWithEmojis(matchFound).isEmpty()) {
            return EmbedCreateFields.Field.of("", "", false);
        }

        String[] bannedChampions = {String.format("`Blue team:` %s", String.join(" ", MatchUtils.getMatchFoundBlueSideBansWithEmojis(matchFound))), String.format("`Red team:` %s", String.join(" ", MatchUtils.getMatchFoundRedSideBansWithEmojis(matchFound))),};

        return EmbedCreateFields.Field.of("> Banned champions", String.join("\n", bannedChampions), false);
    }

    public static EmbedCreateFields.Field getMatchPlayersKdaBlueTeam(JSONObject lastMatch) {
        JSONArray participants = MatchUtils.getMatchParticipants(lastMatch);

        StringBuilder blueTeamParticipants = new StringBuilder();

        for (int i = 0; i < participants.toList().size(); i++) {
            JSONObject participant = participants.getJSONObject(i);

            String summonerName = participant.getString("riotIdGameName").isBlank() ? participant.getString("summonerName") : participant.getString("riotIdGameName");
            String championName = ChampionUtils.getChampionById(String.valueOf(participant.get("championId")));

            int summonerKills = participant.getInt("kills");
            int summonerDeaths = participant.getInt("deaths");
            int summonerAssists = participant.getInt("assists");
            int totalCS = participant.getInt("totalMinionsKilled") + participant.getInt("neutralMinionsKilled");

            String formattedTotalSummonerStats = String.format("(**%s**/**%s**/**%s** **%s CS**)", summonerKills, summonerDeaths, summonerAssists, totalCS);

            if (MatchUtils.isBlueSide(participant.getInt("teamId"))) {
                blueTeamParticipants.append(formatPlayerKdaToFitInOneLine(getEmojiByChampionName(championName), summonerName, formattedTotalSummonerStats));
            }
        }

        return EmbedCreateFields.Field.of("> Blue team", String.join("", blueTeamParticipants), true);
    }

    public static EmbedCreateFields.Field getMatchPlayersKdaRedTeam(JSONObject lastMatch) {
        JSONArray participants = MatchUtils.getMatchParticipants(lastMatch);

        StringBuilder redTeamParticipants = new StringBuilder();

        for (int i = 0; i < participants.toList().size(); i++) {
            JSONObject participant = participants.getJSONObject(i);

            String summonerName = participant.getString("riotIdGameName").isBlank() ? participant.getString("summonerName") : participant.getString("riotIdGameName");
            String championName = ChampionUtils.getChampionById(String.valueOf(participant.get("championId")));

            int summonerKills = participant.getInt("kills");
            int summonerDeaths = participant.getInt("deaths");
            int summonerAssists = participant.getInt("assists");
            int totalCS = participant.getInt("totalMinionsKilled") + participant.getInt("neutralMinionsKilled");

            String formattedTotalSummonerStats = String.format("(**%s**/**%s**/**%s** **%s CS**)", summonerKills, summonerDeaths, summonerAssists, totalCS);

            if (MatchUtils.isRedSide(participant.getInt("teamId"))) {
                redTeamParticipants.append(formatPlayerKdaToFitInOneLine(getEmojiByChampionName(championName), summonerName, formattedTotalSummonerStats));
            }
        }

        return EmbedCreateFields.Field.of("> Red team", String.join("", redTeamParticipants), true);
    }

    public static EmbedCreateFields.Field getTftMatchFoundPlayers(JSONArray participants, JSONArray participantsRankedInfo,  boolean inline) {
        StringBuilder strbParticipants = new StringBuilder();

        String soloQText;

        for (int i = 0; i < participants.toList().size(); i++) {
            JSONObject participant = participants.getJSONObject(i);
            JSONObject rankedInfo = participantsRankedInfo.getJSONObject(i);

            String riotId = participant.getString("riotId");

            int leaguePoints = 0;
            try {
                leaguePoints = rankedInfo.getInt("leaguePoints");
            } catch (JSONException e) {
                System.out.println("League points not found for player: " + riotId);
            }

            String rank = getEmojiByElo("unranked");
            try {
                rank = getEmojiByElo(rankedInfo.getString("tier"));
            } catch (JSONException e) {
                System.out.println("Rank not found for player: " + riotId);
            }

            soloQText = String.format("| (**%s LP**) (**%s W** / **%s L**, %s",
                    leaguePoints,
                    rankedInfo.getInt("wins"),
                    rankedInfo.getInt("losses"),
                    Math.round((rankedInfo.getInt("wins") * 100d) / (rankedInfo.getInt("wins") + rankedInfo.getInt("losses"))) + "%)");

            strbParticipants.append(String.format("%s %s %s\n", rank, riotId, soloQText));
        }

        return EmbedCreateFields.Field.of("> Players", String.join("", strbParticipants), inline);
    }

    public static EmbedCreateFields.Field getMatchFoundBlueSidePlayers(JSONArray participants, boolean inline) {
        StringBuilder blueTeamParticipants = new StringBuilder();

        for (int i = 0; i < participants.toList().size(); i++) {
            JSONObject participant = participants.getJSONObject(i);

            String riotId =  participant.getString("riotId");
            String championName = ChampionUtils.getChampionById(String.valueOf(participant.get("championId")));

            if (MatchUtils.isBlueSide(participant.getInt("teamId"))) {
                blueTeamParticipants.append(String.format("%s %s\n", getEmojiByChampionName(championName), riotId));
            }
        }

        return EmbedCreateFields.Field.of("> Blue team", String.join("", blueTeamParticipants), inline);
    }

    public static EmbedCreateFields.Field getMatchFoundRedSidePlayers(JSONArray participants, boolean inline) {
        StringBuilder redTeamParticipants = new StringBuilder();

        for (int i = 0; i < participants.toList().size(); i++) {
            JSONObject participant = participants.getJSONObject(i);

            String riotId =  participant.getString("riotId");
            String championName = ChampionUtils.getChampionById(String.valueOf(participant.get("championId")));

            if (MatchUtils.isRedSide(participant.getInt("teamId"))) {
                redTeamParticipants.append(String.format("%s %s\n", getEmojiByChampionName(championName), riotId));
            }
        }

        return EmbedCreateFields.Field.of("> Red team", String.join("", redTeamParticipants), true);
    }

    private static String formatPlayerKdaToFitInOneLine(String championEmoji, String summonerName, String playerStats) {
        String stringWithoutEmoji = String.format("%s | %s\n", summonerName, playerStats);

        if (stringWithoutEmoji.length() >= 45) {
            summonerName = summonerName.substring(0, 6) + ".";
        }

        return String.format("%s %s | %s\n", championEmoji, summonerName, playerStats);
    }
}
