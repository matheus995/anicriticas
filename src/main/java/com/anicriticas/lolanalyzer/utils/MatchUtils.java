package com.anicriticas.lolanalyzer.utils;

import com.anicriticas.lolanalyzer.ChampionId;
import com.anicriticas.lolanalyzer.ChampionsEmojis;
import com.anicriticas.lolanalyzer.enums.QueueEnum;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    public static QueueEnum getGameType(int queueId) {
        return switch (queueId) {
            case 400 -> QueueEnum.NORMAL_GAME_DRAFT_PICK;
            case 420 -> QueueEnum.RANKED;
            case 440 -> QueueEnum.FLEX;
            case 450 -> QueueEnum.ARAM;
            case 490 -> QueueEnum.NORMAL_GAME_QUICK_PLAY;
            case 700 -> QueueEnum.CLASH;
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
        Date date = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return dateFormat.format(date);
    }

    public static List<String> getMatchBlueSideBans(JSONObject lastMatch) {
        JSONArray bans = lastMatch.getJSONObject("info").getJSONArray("teams");
        JSONObject blueSide = (JSONObject) bans.get(0);
        JSONArray blueSideBans = blueSide.getJSONArray("bans");

        List<String> banList = new ArrayList<>();

        for (int i = 0; i < blueSideBans.length(); i++) {
            JSONObject ban = blueSideBans.getJSONObject(i);
            banList.add(ChampionId.getChampionById(String.valueOf(ban.get("championId"))));
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
            banList.add(ChampionId.getChampionById(String.valueOf(ban.get("championId"))));
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
            String championName = ChampionId.getChampionById(String.valueOf(ban.get("championId")));
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
            String championName = ChampionId.getChampionById(String.valueOf(ban.get("championId")));
            banList.add(ChampionsEmojis.getEmojiByChampionName(championName) + " " + championName);
        }

        return banList;
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
}
