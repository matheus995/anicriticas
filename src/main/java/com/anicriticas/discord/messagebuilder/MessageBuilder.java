package com.anicriticas.discord.messagebuilder;

import com.anicriticas.entities.match.Match;
import com.anicriticas.entities.match.Participant;
import com.anicriticas.entities.match.lol.LeagueOfLegendsMatch;
import com.anicriticas.enums.ArenaTeam;
import com.anicriticas.enums.QueueEnum;
import com.anicriticas.enums.Region;
import com.anicriticas.enums.TeamSide;
import com.anicriticas.utils.ChampionUtils;
import com.anicriticas.utils.CreateTftImageUtils;
import com.anicriticas.utils.MatchUtils;
import com.anicriticas.utils.TftUtils;
import discord4j.core.spec.EmbedCreateFields;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

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
            }
        }

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

    public static String getFinishedMatchResultArena(JSONObject lastMatch) {
        return MatchUtils.getGameType(lastMatch.getJSONObject("info").getInt("queueId")).getQueueDescription();
    }

    public static EmbedCreateFields.Field getMatchInformation(JSONObject lastMatch) {
        String matchDate = MatchUtils.getMatchDate(lastMatch.getJSONObject("info").getLong("gameStartTimestamp"));
        String matchDuration = MatchUtils.getMatchDuration(lastMatch.getJSONObject("info").getLong("gameStartTimestamp"), lastMatch.getJSONObject("info").getLong("gameEndTimestamp"));
        String[] matchInformation = {String.format("`Date:` %s", matchDate), String.format("`Duration:` %s", matchDuration)};

        return EmbedCreateFields.Field.of("> Match information", String.join("\n", matchInformation), false);
    }

    public static EmbedCreateFields.Field getTftMatchInformation(JSONObject finishedMatch, JSONObject player) {
        String matchDate = MatchUtils.getMatchDate(finishedMatch.getJSONObject("info").getLong("game_datetime"));
        QueueEnum queueType = MatchUtils.getGameType(finishedMatch.getJSONObject("info").getInt("queueId"));
        String matchDuration = MatchUtils.getMatchDuration(finishedMatch.getJSONObject("info").getLong("gameCreation"), finishedMatch.getJSONObject("info").getLong("game_datetime"));

        String[] matchInformation = {
                String.format("`Date:` %s", matchDate),
                String.format("`Duration:` %s", matchDuration),
                String.format("`Game Type:` %s", queueType.getQueueDescription()),
                String.format("`Player:` %s", player.get("riotIdGameName")),
        };

        return EmbedCreateFields.Field.of("> Match information", String.join("\n", matchInformation), false);
    }

    public static EmbedCreateFields.Field getMatchFoundInformation(JSONObject matchFound) {
        String matchDate = MatchUtils.getMatchDate(matchFound.getLong("gameStartTime"));
        QueueEnum queueType = MatchUtils.getGameType(matchFound.getInt("gameQueueConfigId"));
        String[] matchInformation = {String.format("`Date:` %s", matchDate), String.format("`Game Type:` %s", queueType.getQueueDescription())};

        return EmbedCreateFields.Field.of("> Match information", String.join("\n", matchInformation), false);
    }

    public static EmbedCreateFields.Field getMatchFoundInformation2(Match match) {
        String[] matchInformation = {String.format("`Date:` %s", match.getStartDate()), String.format("`Game Type:` %s", match.getQueueType().getQueueDescription())};

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

    public static EmbedCreateFields.Field getMatchBansArena(JSONObject finishedMatch) {
        if (MatchUtils.getMatchBlueSideBans(finishedMatch).isEmpty()) {
            return EmbedCreateFields.Field.of("", "", false);
        }

        String[] bannedChampions = {String.format("%s", String.join(" ", MatchUtils.getMatchBlueSideBansWithEmojis(finishedMatch))), String.format("%s", String.join(" ", MatchUtils.getMatchRedSideBansWithEmojis(finishedMatch))),};

        return EmbedCreateFields.Field.of("> Banned champions", String.join("\n", bannedChampions), false);
    }

    public static EmbedCreateFields.Field getMatchFoundBans(JSONObject matchFound) {
        if (MatchUtils.getMatchFoundBlueSideBansWithEmojis(matchFound).isEmpty()) {
            return EmbedCreateFields.Field.of("", "", false);
        }

        String[] bannedChampions = {String.format("`Blue team:` %s", String.join(" ", MatchUtils.getMatchFoundBlueSideBansWithEmojis(matchFound))), String.format("`Red team:` %s", String.join(" ", MatchUtils.getMatchFoundRedSideBansWithEmojis(matchFound))),};

        return EmbedCreateFields.Field.of("> Banned champions", String.join("\n", bannedChampions), false);
    }

    public static EmbedCreateFields.Field getMatchFoundBans2(LeagueOfLegendsMatch match) {
        if (match.getBannedChampions().isEmpty()) {
            return EmbedCreateFields.Field.of("", "", false);
        }

        String[] bannedChampions;

        if (match.getQueueType().equals(QueueEnum.ARENA)) {
            bannedChampions = new String[]{String.format("%s", String.join(" ", MatchUtils.getMatchFoundBansWithEmojis2(match, TeamSide.BLUE)))};
        } else {
           bannedChampions = new String[]{String.format("`Blue team:` %s", String.join(" ", MatchUtils.getMatchFoundBansWithEmojis2(match, TeamSide.BLUE))),
                   String.format("`Red team:` %s", String.join(" ", MatchUtils.getMatchFoundBansWithEmojis2(match, TeamSide.RED)))};
        }

        return EmbedCreateFields.Field.of("> Banned champions", String.join("\n", bannedChampions), false);
    }

    public static EmbedCreateFields.Field getMatchFoundBansArena(JSONObject matchFound) {
        if (MatchUtils.getMatchFoundBlueSideBansWithEmojis(matchFound).isEmpty()) {
            return EmbedCreateFields.Field.of("", "", false);
        }

        String[] bannedChampions = {String.format("%s", String.join(" ", MatchUtils.getMatchFoundBlueSideBansWithEmojis(matchFound))), String.format("%s", String.join(" ", MatchUtils.getMatchFoundRedSideBansWithEmojis(matchFound))),};

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

            int wins = 0;
            int loses = 0;
            int leaguePoints = 0;
            String rank = getEmojiByElo("unranked");

            if (Objects.nonNull(participantsRankedInfo.toList().get(i))) {
                JSONObject rankedInfo = participantsRankedInfo.getJSONObject(i);
                wins = rankedInfo.getInt("wins");
                loses = rankedInfo.getInt("losses");
                rank = getEmojiByElo(rankedInfo.getString("tier"));
                leaguePoints = rankedInfo.getInt("leaguePoints");
            }

            String riotId = participant.getString("riotId");

            soloQText = String.format("| (**%s LP**) (**%s W** / **%s L**, %s",
                    leaguePoints,
                    wins,
                    loses,
                    Math.round((wins * 100d) / (wins +loses)) + "%)");

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

    public static EmbedCreateFields.Field getMatchFoundPlayers2(List<Participant> participants, TeamSide teamSide, boolean inline) {
        StringBuilder teamParticipants = new StringBuilder();

        for (Participant participant : participants) {
            if (participant.getTeamSide().equals(teamSide)) {
                teamParticipants.append(String.format("%s %s\n", getEmojiByChampionName(participant.getChampion()), participant.getRiotId()));
            }
        }

        if (teamSide.equals(TeamSide.BLUE)) {
            return EmbedCreateFields.Field.of("> Blue team", String.join("", teamParticipants), inline);
        }

        return EmbedCreateFields.Field.of("> Red team", String.join("", teamParticipants), inline);
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

//    public static EmbedCreateFields.Field getMatchFoundArenaPlayers(JSONArray participants, boolean inline) {
//        StringBuilder blueTeamParticipants = new StringBuilder();
//
//        for (int i = 0; i < participants.toList().size(); i++) {
//            JSONObject participant = participants.getJSONObject(i);
//
//            String riotId =  participant.getString("riotId");
//            String championName = ChampionUtils.getChampionById(String.valueOf(participant.get("championId")));
//
//            if (MatchUtils.isBlueSide(participant.getInt("teamId"))) {
//                blueTeamParticipants.append(String.format("%s %s\n", getEmojiByChampionName(championName), riotId));
//            }
//        }
//
//        return EmbedCreateFields.Field.of("> Players", String.join("", blueTeamParticipants), inline);
//    }

    public static EmbedCreateFields.Field getMatchFoundArenaPlayers(JSONArray participants, JSONArray participantsRankedInfo, boolean inline) {
        StringBuilder strbParticipants = new StringBuilder();

        String soloQText;

        for (int i = 0; i < participants.toList().size(); i++) {
            JSONObject participant = participants.getJSONObject(i);
            JSONObject rankedInfo = null;

            for (int j = 0; j < participantsRankedInfo.toList().size(); j++) {
                rankedInfo = participantsRankedInfo.getJSONObject(j);

                if(participant.getString("puuid").equals(rankedInfo.getString("puuid"))) {
                    break;
                }
            }

            String riotId = participant.getString("riotId");

            int wins = 0;
            int loses = 0;
            int leaguePoints = 0;
            String rank = getEmojiByElo("unranked");

            if (Objects.nonNull(rankedInfo)) {
                leaguePoints = rankedInfo.getInt("leaguePoints");
                rank = getEmojiByElo(rankedInfo.getString("tier"));
                wins = rankedInfo.getInt("wins");
                loses = rankedInfo.getInt("losses");
            }

            soloQText = String.format("| (**%s LP**) (**%s W** / **%s L**, %s",
                    leaguePoints,
                    wins,
                    loses,
                    Math.round((wins * 100d) / (wins + loses)) + "%)");

            String championName = ChampionUtils.getChampionById(String.valueOf(participant.get("championId")));

//            strbParticipants.append(String.format("%s %s %s %s\n", rank, getEmojiByChampionName(championName), riotId, soloQText));
//            strbParticipants.append(String.format("%s %s %s\n", rank, getEmojiByChampionName(championName), riotId));
            strbParticipants.append(String.format("%s %s\n", getEmojiByChampionName(championName), riotId));
        }

        return EmbedCreateFields.Field.of("> Players", String.join("", strbParticipants), inline);
    }

    public static EmbedCreateFields.Field getMatchFoundArenaPlayers2(List<Participant> participants, JSONArray participantsRankedInfo, boolean inline) {
        StringBuilder strbParticipants = new StringBuilder();

        String soloQText;

        for (Participant participant : participants) {
            JSONObject rankedInfo = null;

            for (int j = 0; j < participantsRankedInfo.toList().size(); j++) {
                rankedInfo = participantsRankedInfo.getJSONObject(j);

                if(participant.getPuuid().equals(rankedInfo.getString("puuid"))) {
                    break;
                }
            }

            int wins = 0;
            int loses = 0;
            int leaguePoints = 0;
            String rank = getEmojiByElo("unranked");

            if (Objects.nonNull(rankedInfo)) {
                leaguePoints = rankedInfo.getInt("leaguePoints");
                rank = getEmojiByElo(rankedInfo.getString("tier"));
                wins = rankedInfo.getInt("wins");
                loses = rankedInfo.getInt("losses");
            }

            soloQText = String.format("| (**%s LP**) (**%s W** / **%s L**, %s",
                    leaguePoints,
                    wins,
                    loses,
                    Math.round((wins * 100d) / (wins + loses)) + "%)");

            strbParticipants.append(String.format("%s %s\n", getEmojiByChampionName(participant.getChampion()), participant.getRiotId()));
        }

        return EmbedCreateFields.Field.of("> Players", String.join("", strbParticipants), inline);
    }

//    public static EmbedCreateFields.Field getArenaTeamStats(JSONArray participants, int teamId, String teamType, boolean inline) {
    public static EmbedCreateFields.Field getArenaTeamStats(JSONArray participants, int placement, boolean inline) {
        ArenaTeam arenaTeam = null;
        StringBuilder teamStats = new StringBuilder();

        for (int i = 0; i < participants.toList().size(); i++) {
            JSONObject participant = participants.getJSONObject(i);

            if (participant.getInt("placement") != placement) {
                continue;
            }

            String summonerName = participant.getString("riotIdGameName").isBlank() ? participant.getString("summonerName") : participant.getString("riotIdGameName");
            String championName = ChampionUtils.getChampionById(String.valueOf(participant.get("championId")));

            int summonerKills = participant.getInt("kills");
            int summonerDeaths = participant.getInt("deaths");
            int summonerAssists = participant.getInt("assists");
            arenaTeam = ArenaTeam.getTeamById(participant.getInt("playerSubteamId"));

            String formattedTotalSummonerStats = String.format("(**%s**/**%s**/**%s**)", summonerKills, summonerDeaths, summonerAssists);

            teamStats.append(formatPlayerKdaToFitInOneLine(getEmojiByChampionName(championName), summonerName, formattedTotalSummonerStats));
        }

        return EmbedCreateFields.Field.of("> " + placement + ". Team " + arenaTeam, String.join("", teamStats), inline);
    }

    public static EmbedCreateFields.Field getArenaTeamStats2(JSONArray participants, int teamId, String teamType, boolean inline) {
        StringBuilder teamStats = new StringBuilder();

        // Mapa para agrupar participantes por placement
        Map<Integer, List<JSONObject>> placementMap = new TreeMap<>();

        // Agrupar jogadores por placement
        for (int i = 0; i < participants.toList().size(); i++) {
            JSONObject participant = participants.getJSONObject(i);

            int placement = participant.getInt("placement"); // Supondo que o JSON tenha esse campo
            if (!placementMap.containsKey(placement)) {
                placementMap.put(placement, new ArrayList<>());
            }
            placementMap.get(placement).add(participant);
        }

        // Iterar pelos placements ordenados
        for (Map.Entry<Integer, List<JSONObject>> entry : placementMap.entrySet()) {
            int placement = entry.getKey();
            List<JSONObject> participantsAtPlacement = entry.getValue();

            // Ordenar os jogadores com o mesmo placement, se necessário (por exemplo, por nome)
            participantsAtPlacement.sort(Comparator.comparing(p -> p.getString("summonerName")));

            // Adicionar os jogadores desse placement ao StringBuilder
            for (JSONObject participant : participantsAtPlacement) {
                String summonerName = participant.getString("riotIdGameName").isBlank() ? participant.getString("summonerName") : participant.getString("riotIdGameName");
                String championName = ChampionUtils.getChampionById(String.valueOf(participant.get("championId")));

                int summonerKills = participant.getInt("kills");
                int summonerDeaths = participant.getInt("deaths");
                int summonerAssists = participant.getInt("assists");

                String formattedTotalSummonerStats = String.format("(**%s**/**%s**/**%s**)", summonerKills, summonerDeaths, summonerAssists);

                if (participant.getInt("playerSubteamId") == teamId) {
                    teamStats.append(formatPlayerKdaToFitInOneLine(getEmojiByChampionName(championName), summonerName, formattedTotalSummonerStats));
                }
            }
        }

        return EmbedCreateFields.Field.of("> " + teamId + ". Team " + teamType, String.join("", teamStats), inline);
    }

    public static EmbedCreateFields.Field getTftPlayerTraits(JSONObject finishedMatch, String puuid) {
        JSONArray matchParticipants = finishedMatch.getJSONObject("info").getJSONArray("participants");

        JSONObject participant = MatchUtils.getParticipantByPuuid(puuid, matchParticipants);

        return EmbedCreateFields.Field.of("> Traits", TftUtils.getUnitsFromParticipant(Objects.requireNonNull(participant)).toString(), false);
    }

    private static String formatPlayerKdaToFitInOneLine(String championEmoji, String summonerName, String playerStats) {
        String stringWithoutEmoji = String.format("%s | %s\n", summonerName, playerStats);

        if (stringWithoutEmoji.length() >= 45) {
            summonerName = summonerName.substring(0, 6) + ".";
        }

        return String.format("%s %s | %s\n", championEmoji, summonerName, playerStats);
    }
}
