package com.anicriticas.utils;

import com.anicriticas.entities.valorant.Match;
import com.anicriticas.entities.valorant.MatchResult;
import com.anicriticas.entities.valorant.Player;
import com.anicriticas.entities.valorant.PlayerStats;
import com.anicriticas.model.Players;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExtractValorantMatchInfo {

    public static Match extractData(JSONObject rawMatchData) {
        JSONObject matchData = rawMatchData.getJSONArray("data").getJSONObject(0);

        Match match = new Match();

        JSONObject matchInfo = matchData.getJSONObject("metadata");
        match.setId(matchInfo.getString("match_id"));
        match.setStartDate(matchInfo.getString("started_at"));
        match.setMap(matchInfo.getJSONObject("map").getString("name"));
        match.setMatchType(matchInfo.getJSONObject("queue").getString("id"));
        match.setMatchDuration(matchInfo.getLong("game_length_in_ms"));

        List<Players> players = extractPlayers(matchData.getJSONArray("players"));
        match.setPlayers(players);

        JSONArray teams = matchData.getJSONArray("teams");
        match.setMatchResult(extractMatchResult(teams));

        return match;
    }

    private static List<Players> extractPlayers(JSONArray jsonArray) {

        List<Players> playersList = new ArrayList<>();

        for (int i = 0; i < jsonArray.toList().size(); i++) {
            JSONObject playerJson = jsonArray.getJSONObject(i);

            Players player = new Players();
            PlayerStats playerStats = new PlayerStats();

//            player.setPuuid((UUID) playerJson.get("puuid"));
            player.setPuuid(UUID.fromString(playerJson.getString("puuid")));
            player.setName(playerJson.getString("name"));
            player.setTag(playerJson.getString("tag"));
            player.setTeam(playerJson.getString("team_id"));
            player.setAgent(playerJson.getJSONObject("agent").getString("name"));
            player.setRanking(playerJson.getJSONObject("tier").getString("name"));

            playerStats.setKills(playerJson.getJSONObject("stats").getInt("kills"));
            playerStats.setDeaths(playerJson.getJSONObject("stats").getInt("deaths"));
            playerStats.setAssists(playerJson.getJSONObject("stats").getInt("assists"));
            playerStats.setScore(playerJson.getJSONObject("stats").getInt("score"));

            player.setPlayerStats(playerStats);

            playersList.add(player);
        }

        return playersList;
    }

    private static MatchResult extractMatchResult(JSONArray teamsData) {

        MatchResult matchResult = new MatchResult();

        for (int i = 0; i < teamsData.toList().size(); i++) {
            JSONObject teamJson = teamsData.getJSONObject(i);

            int qtdRoundsWin = teamJson.getJSONObject("rounds").getInt("won");
            int qtdRoundsLose = teamJson.getJSONObject("rounds").getInt("lost");

            if (qtdRoundsWin >= qtdRoundsLose) {
                matchResult.setWinner(qtdRoundsWin == qtdRoundsLose ? "Draw" : teamJson.getString("team_id"));
                matchResult.setRoundsWinner(qtdRoundsWin);
                matchResult.setRoundsLoser(qtdRoundsLose);
            }
        }
        return matchResult;
    }
}