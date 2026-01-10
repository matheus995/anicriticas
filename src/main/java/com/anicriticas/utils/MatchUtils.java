package com.anicriticas.utils;

import com.anicriticas.emojis.ChampionsEmojis;
import com.anicriticas.entities.match.lol.BannedChampion;
import com.anicriticas.entities.match.lol.LeagueOfLegendsMatch;
import com.anicriticas.entities.valorant.Match;
import com.anicriticas.entities.valorant.Player;
import com.anicriticas.enums.QueueEnum;
import com.anicriticas.enums.TeamSide;
import com.anicriticas.model.Players;
import discord4j.rest.util.Color;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MatchUtils {

    public static JSONObject getParticipantBySummonerPuuid(String puuid, JSONObject match) {
        try {
            JSONArray participants = (JSONArray) match.getJSONObject("info").get("participants");

            for (int i = 0; i < participants.length(); i++) {
                JSONObject participant = participants.getJSONObject(i);

                if (participant.getString("puuid").equals(puuid)) {
                    return participant;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return null;
    }

    public static JSONArray getMatchParticipants(JSONObject match) {
        return match.getJSONObject("info").getJSONArray("participants");
    }

    public static String getMatchResult(JSONObject participant) {
        if (participant.getBoolean("win")) {
            return "Victory";
        }
        return "Defeat";
    }

    public static String getMatchResult(boolean win) {
        if (win) {
            return "Victory";
        }
        return "Defeat";
    }

    public static QueueEnum getGameType(int queueId) {
        return switch (queueId) {
            case 400 -> QueueEnum.NORMAL_GAME_DRAFT_PICK;
            case 420, 1100 -> QueueEnum.RANKED;
            case 440 -> QueueEnum.FLEX;
            case 450 -> QueueEnum.ARAM;
            case 490 -> QueueEnum.NORMAL_GAME_QUICK_PLAY;
            case 700 -> QueueEnum.CLASH;
            case 1020 -> QueueEnum.ONE_FOR_ALL;
            case 1090 -> QueueEnum.NORMAL_GAME;
            case 1700, 1710 -> QueueEnum.ARENA;
            case 900, 1900 -> QueueEnum.URF;
            default -> QueueEnum.UNKNOWN_GAME_TYPE;
        };
    }

    public static String getTeamSide(int teamId) {
        return switch (teamId) {
            case 100 -> "BLUE";
            case 200 -> "RED";
            default -> "UNKNOWN TEAM TYPE";
        };
    }

    public static boolean isBlueSide(int teamId) {
        return teamId == 100;
    }

    public static boolean isRedSide(int teamId) {
        return teamId == 200;
    }

    public static String getMatchDuration(long start, long end) {
        long durationInMillis = end - start;

        long hours = TimeUnit.MILLISECONDS.toHours(durationInMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationInMillis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationInMillis) % 60;

        StringBuilder duration = new StringBuilder();

        if (hours != 0) {
            duration.append(String.format("%02dh", hours));
        }

        duration.append(String.format("%02dm", minutes));
        duration.append(String.format("%02ds", seconds));

        return duration.toString();
    }

    public static String getMatchDate(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        LocalDateTime matchDate = LocalDateTime.ofInstant(instant, ZoneId.of("GMT-3"));

        return matchDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    public static List<String> getMatchBlueSideBans(JSONObject lastMatch) {
        JSONArray bans = lastMatch.getJSONObject("info").getJSONArray("teams");
        JSONObject blueSide = (JSONObject) bans.get(0);
        JSONArray blueSideBans = blueSide.getJSONArray("bans");

        List<String> banList = new ArrayList<>();

        for (int i = 0; i < blueSideBans.length(); i++) {
            JSONObject ban = blueSideBans.getJSONObject(i);
            banList.add(ChampionUtils.getChampionById(String.valueOf(ban.get("championId"))));
        }

        return banList;
    }

    public static List<String> getMatchRedSideBans(JSONObject lastMatch) {
        JSONArray bans = lastMatch.getJSONObject("info").getJSONArray("teams");
        JSONObject blueSide = (JSONObject) bans.get(1);
        JSONArray blueSideBans = blueSide.getJSONArray("bans");

        List<String> banList = new ArrayList<>();

        for (int i = 0; i < blueSideBans.length(); i++) {
            JSONObject ban = blueSideBans.getJSONObject(i);
            banList.add(ChampionUtils.getChampionById(String.valueOf(ban.get("championId"))));
        }

        return banList;
    }

    public static List<String> getMatchBlueSideBansWithEmojis(JSONObject lastMatch) {
        JSONArray bans = lastMatch.getJSONObject("info").getJSONArray("teams");
        JSONObject blueSide = (JSONObject) bans.get(0);
        JSONArray blueSideBans = blueSide.getJSONArray("bans");

        List<String> banList = new ArrayList<>();

        for (int i = 0; i < blueSideBans.length(); i++) {
            JSONObject ban = blueSideBans.getJSONObject(i);
            String championName = ChampionUtils.getChampionById(String.valueOf(ban.get("championId")));
            banList.add(ChampionsEmojis.getEmojiByChampionName(championName) + " " + championName);
        }

        return banList;
    }

    public static List<String> getMatchRedSideBansWithEmojis(JSONObject lastMatch) {
        JSONArray bans = lastMatch.getJSONObject("info").getJSONArray("teams");
        JSONObject redSide = (JSONObject) bans.get(1);
        JSONArray redSideBans = redSide.getJSONArray("bans");

        List<String> banList = new ArrayList<>();

        for (int i = 0; i < redSideBans.length(); i++) {
            JSONObject ban = redSideBans.getJSONObject(i);
            String championName = ChampionUtils.getChampionById(String.valueOf(ban.get("championId")));
            banList.add(ChampionsEmojis.getEmojiByChampionName(championName) + " " + championName);
        }

        return banList;
    }

    public static List<String> getMatchFoundBlueSideBansWithEmojis(JSONObject matchFound) {
        JSONArray bans = matchFound.getJSONArray("bannedChampions");

        List<String> banList = new ArrayList<>();

        for (int i = 0; i < bans.toList().size(); i++) {
            JSONObject ban = bans.getJSONObject(i);

            if (isBlueSide(ban.getInt("teamId"))) {
                String championName = ChampionUtils.getChampionById(String.valueOf(ban.get("championId")));
                banList.add(ChampionsEmojis.getEmojiByChampionName(championName) + " " + championName);
            }
        }

        return banList;
    }

    public static List<String> getMatchFoundBansWithEmojis2(LeagueOfLegendsMatch match, TeamSide teamSide) {
        List<String> banList = new ArrayList<>();

        for (int i = 0; i < match.getBannedChampions().size(); i++) {
            BannedChampion bannedChampion = match.getBannedChampions().get(i);

            if (bannedChampion.getTeamSide().equals(teamSide)) {
                banList.add(ChampionsEmojis.getEmojiByChampionName(bannedChampion.getChampion()) + " " + bannedChampion.getChampion());
            }
        }

        return banList;
    }

    public static List<String> getMatchFoundRedSideBansWithEmojis(JSONObject matchFound) {
        JSONArray bans = matchFound.getJSONArray("bannedChampions");

        List<String> banList = new ArrayList<>();

        for (int i = 0; i < bans.toList().size(); i++) {
            JSONObject ban = bans.getJSONObject(i);

            if (isRedSide(ban.getInt("teamId"))) {
                String championName = ChampionUtils.getChampionById(String.valueOf(ban.get("championId")));
                banList.add(ChampionsEmojis.getEmojiByChampionName(championName) + " " + championName);
            }
        }

        return banList;
    }

    public static String getFinishedMatchResult(String playerSide, JSONObject match) {
        JSONArray participants = getMatchParticipants(match);

        for (int i = 0; i < participants.toList().size(); i++) {
            JSONObject participant = participants.getJSONObject(i);

            String participantSide = MatchUtils.getTeamSide(participant.getInt("teamId"));
            if (participantSide.equals(playerSide)) {
                return MatchUtils.getMatchResult(participant.getBoolean("win"));
            }
        }

        return null;
    }

    public static Color getFinishedMatchColor(String playerSide, JSONObject match) {
        JSONArray participants = getMatchParticipants(match);

        for (int i = 0; i < participants.toList().size(); i++) {
            JSONObject participant = participants.getJSONObject(i);

            String participantSide = MatchUtils.getTeamSide(participant.getInt("teamId"));
            if (participantSide.equals(playerSide)) {
                return getColorByMatchResult(participant.getBoolean("win"));
            }
        }

        return null;
    }

    public static Color getValorantFinishedMatchColor(Players player, Match match) {

         if (player.getTeam().equalsIgnoreCase(match.getMatchResult().getWinner())) {
            return Color.GREEN;
        } else if (match.getMatchResult().getWinner().equalsIgnoreCase("Draw")) {
            return Color.YELLOW;
        }

        return Color.RED;
    }

    public static String getValorantFinishedMatchResult(Players player, Match match) {

        if (player.getTeam().equalsIgnoreCase(match.getMatchResult().getWinner())) {
            return "Win";
        } else if (match.getMatchResult().getWinner().equalsIgnoreCase("Draw")) {
            return "Draw";
        }

        return "Lose";
    }

    public static Color getColorByMatchResult(boolean matchResult) {
        if (matchResult) {
            return Color.GREEN;
        }
        return Color.RED;
    }

    public static boolean queueTypeHasBans(int queueId) {
        return switch (queueId) {
            case 420, 440 -> true;
            default -> false;
        };
    }

    public static String humanReadableInt(long number) {
        DecimalFormat oneDecimal = new DecimalFormat("0.0");

        long absNumber = Math.abs(number);
        double result;
        String suffix = "";

        if (absNumber < 1_000) {
            result = number;
        } else if (absNumber < 1_000_000) {
            result = number / 1_000.0;
            suffix = "K";
        } else if (absNumber < 1_000_000_000) {
            result = number / 1_000_000.0;
            suffix = "M";
        } else {
            result = number / 1_000_000_000.0;
            suffix = "B";
        }

        return oneDecimal.format(result) + suffix;
    }

    public static JSONObject getParticipantByPuuid(String participantUuid, JSONArray participants) {
        for (int i = 0; i < participants.toList().size(); i++) {
            JSONObject participant = participants.getJSONObject(i);

            if (participant.get("puuid").equals(participantUuid)) {
                return participant;
            }
        }
        return null;
    }

    public static boolean isDateInRange(String date, Long dateRange) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
        LocalDateTime dateToVerify = LocalDateTime.parse(date, formatter);

        LocalDateTime dateNow = LocalDateTime.now();

        LocalDateTime dateDaysEarlier = dateNow.minusDays(dateRange);

        return dateToVerify.isAfter(dateDaysEarlier);
    }

    public static String getValorantFinishedMatchScore(Match match, String matchResult) {

        if (matchResult.equalsIgnoreCase("Lose")) {
            return match.getMatchResult().getRoundsLoser() + " - " + match.getMatchResult().getRoundsWinner();
        }

        return match.getMatchResult().getRoundsWinner() + " - " + match.getMatchResult().getRoundsLoser();

    }
}
