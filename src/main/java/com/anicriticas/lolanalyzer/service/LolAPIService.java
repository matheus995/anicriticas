package com.anicriticas.lolanalyzer.service;

import com.anicriticas.lolanalyzer.endpoints.*;
import com.anicriticas.lolanalyzer.enums.RegionEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service
public class LolAPIService {

    private final String riotToken = System.getenv("RIOT_TOKEN");

    @Autowired
    private RestTemplate restTemplate = new RestTemplate();

    public String getRiotAccountByNameAndId(String riotNickName, String riotId, RegionEnum region) {
        String url = General.getAlternativeRegionBaseUrl(region) + AccountV1.GET_RIOT_ACCOUNT;

        final HttpHeaders headers = new HttpHeaders();
        headers.set("X-Riot-Token", riotToken);

        final HttpEntity<String> entity = new HttpEntity<>(headers);

        Map<String, String> pathParam = new HashMap<>();
        pathParam.put("gameName", riotNickName);
        pathParam.put("tagLine", riotId);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class, pathParam);
            return new JSONObject(responseEntity.getBody()).toString(4);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error when trying to find riot account " + riotNickName + " " + riotId + ": " + e.getMessage());
        }
    }

    public String getRiotAccountByPuuid(String puuid, RegionEnum region) {
        String url = General.getAlternativeRegionBaseUrl(region) + AccountV1.GET_RIOT_ACCOUNT_BY_PUUID;

        final HttpHeaders headers = new HttpHeaders();
        headers.set("X-Riot-Token", riotToken);

        final HttpEntity<String> entity = new HttpEntity<>(headers);

        Map<String, String> pathParam = new HashMap<>();
        pathParam.put("puuid", puuid);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class, pathParam);
            return new JSONObject(responseEntity.getBody()).toString(4);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error when trying to find riot account " + puuid + ": " + e.getMessage());
        }
    }

    public String getSummonerByPuuid(String puuid, RegionEnum region) {
        String url = General.getRegionBaseUrl(region) + SummonerV4.GET_SUMMONER_BY_PUUID;

        final HttpHeaders headers = new HttpHeaders();
        headers.set("X-Riot-Token", riotToken);

        final HttpEntity<String> entity = new HttpEntity<>(headers);

        Map<String, String> pathParam = new HashMap<>();
        pathParam.put("encryptedPUUID", puuid);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class, pathParam);
            return new JSONObject(responseEntity.getBody()).toString(4);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error when trying to find data from summoner " + puuid + ": " + e.getMessage());
        }
    }

    public String getSummonerByName(String summonerName, RegionEnum region) {
        String url = General.getRegionBaseUrl(region) + SummonerV4.GET_SUMMONER_BY_NAME;

        final HttpHeaders headers = new HttpHeaders();
        headers.set("X-Riot-Token", riotToken);

        final HttpEntity<String> entity = new HttpEntity<>(headers);

        Map<String, String> pathParam = new HashMap<>();
        pathParam.put("summonerName", summonerName);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class, pathParam);
            return new JSONObject(responseEntity.getBody()).toString(4);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error when trying to find data from summoner " + summonerName + ": " + e.getMessage());
        }
    }

    public String[] getLastMatchesIdsBySummonerPuuid(String puuid, String count, RegionEnum region) {
        String url = General.getAlternativeRegionBaseUrl(region) + MatchV5.GET_LAST_MATCHS_ID_BY_SUMMONER_PUUID;

        final HttpHeaders headers = new HttpHeaders();
        headers.set("X-Riot-Token", riotToken);

        final HttpEntity<String> entity = new HttpEntity<>(headers);

        URI uri = UriComponentsBuilder.fromUriString(url)
                .queryParam("start", 0)
                .queryParam("count", count)
                .buildAndExpand(puuid)
                .toUri();

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
            String[] matchId = null;
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                matchId = objectMapper.readValue(responseEntity.getBody(), String[].class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert matchId != null;
            return matchId;
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error when trying to retrieve last matches IDs from Summoner PUUID: " + puuid + ": " + e.getMessage());
        }
    }

    public JSONObject getMatchById(String matchId, RegionEnum region) {
        String url = General.getAlternativeRegionBaseUrl(region) + MatchV5.GET_MATCH_BY_MATCHID;

        final HttpHeaders headers = new HttpHeaders();
        headers.set("X-Riot-Token", riotToken);
        headers.set(HttpHeaders.ACCEPT, "application/json");

        final HttpEntity<String> entity = new HttpEntity<>(headers);

        Map<String, String> pathParam = new HashMap<>();
        pathParam.put("matchId", matchId);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class, pathParam);
            return new JSONObject(responseEntity.getBody());
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error when trying to retrieve last match id: " + matchId + ": " + e.getMessage());
        }
    }

    public JSONObject getFinishedMatchById(String matchId, RegionEnum region) {
        String url = General.getAlternativeRegionBaseUrl(region) + MatchV5.GET_MATCH_BY_MATCHID;

        final HttpHeaders headers = new HttpHeaders();
        headers.set("X-Riot-Token", riotToken);
        headers.set(HttpHeaders.ACCEPT, "application/json");

        final HttpEntity<String> entity = new HttpEntity<>(headers);

        Map<String, String> pathParam = new HashMap<>();
        pathParam.put("matchId", matchId);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class, pathParam);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return new JSONObject(responseEntity.getBody());
            }
            return null;
        } catch (HttpClientErrorException e) {
            return null;
        }
    }

    public JSONArray getTopChampionsMasteryBySummonerPuuid(String puuid, RegionEnum region) {
        String url = General.getRegionBaseUrl(region) + ChampionMasteryV4.GET_TOP_CHAMPION_MASTERY;

        final HttpHeaders headers = new HttpHeaders();
        headers.set("X-Riot-Token", riotToken);
        headers.set(HttpHeaders.ACCEPT, "application/json");

        final HttpEntity<String> entity = new HttpEntity<>(headers);

        Map<String, String> pathParam = new HashMap<>();
        pathParam.put("encryptedPUUID", puuid);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class, pathParam);
            return new JSONArray(responseEntity.getBody());
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error when trying to retrieve top champions mastery " + e.getMessage());
        }
    }

    public JSONArray getRankedStats(String summonerId, RegionEnum region) {
        String url = General.getRegionBaseUrl(region) + LeagueV4.GET_RANKED_STATS_BY_SUMMONER_ID;

        final HttpHeaders headers = new HttpHeaders();
        headers.set("X-Riot-Token", riotToken);
        headers.set(HttpHeaders.ACCEPT, "application/json");

        final HttpEntity<String> entity = new HttpEntity<>(headers);

        Map<String, String> pathParam = new HashMap<>();
        pathParam.put("encryptedSummonerId", summonerId);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class, pathParam);
            return new JSONArray(responseEntity.getBody());
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error when trying to retrieve top champions mastery " + e.getMessage());
        }
    }

    public JSONObject getActiveGamesBySummonerId(String summonerId, RegionEnum region) {
        String url = General.getRegionBaseUrl(region) + SpectatorV4.GET_ACTIVE_GAMES_BY_SUMMONER_ID;

        final HttpHeaders headers = new HttpHeaders();
        headers.set("X-Riot-Token", riotToken);
        headers.set(HttpHeaders.ACCEPT, "application/json");

        final HttpEntity<String> entity = new HttpEntity<>(headers);

        Map<String, String> pathParam = new HashMap<>();
        pathParam.put("encryptedSummonerId", summonerId);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class, pathParam);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return new JSONObject(responseEntity.getBody());
            }
            return null;
        } catch (HttpClientErrorException e) {
            return null;
        }
    }
}
