package com.anicriticas.service;

import com.anicriticas.endpoints.*;
import com.anicriticas.enums.Region;
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
public class TftAPIService {

    private final String riotToken = System.getenv("RIOT_TOKEN_TFT");

    @Autowired
    private RestTemplate restTemplate = new RestTemplate();

    public JSONObject getFinishedMatchById(String matchId, Region region) {
        String url = General.getAlternativeRegionBaseUrl(region) + TftMatchV1.GET_MATCH_BY_MATCHID;

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

    public JSONArray getRankedStats(String puuid, Region region) {
        String url = General.getRegionBaseUrl(region) + TftLeagueV1.GET_RANKED_STATS_BY_PUUID;

        final HttpHeaders headers = new HttpHeaders();
        headers.set("X-Riot-Token", riotToken);
        headers.set(HttpHeaders.ACCEPT, "application/json");

        final HttpEntity<String> entity = new HttpEntity<>(headers);

        Map<String, String> pathParam = new HashMap<>();
        pathParam.put("puuid", puuid);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class, pathParam);
            return new JSONArray(responseEntity.getBody());
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error when trying to retrieve TFT player info " + e.getMessage());
        }
    }

    public JSONObject getActiveGamesByPuuid(String puuid, Region region) {
        String url = General.getRegionBaseUrl(region) + SpectatorTftV5.GET_ACTIVE_GAMES_BY_SUMMONER_ID;

        final HttpHeaders headers = new HttpHeaders();
        headers.set("X-Riot-Token", riotToken);
        headers.set(HttpHeaders.ACCEPT, "application/json");

        final HttpEntity<String> entity = new HttpEntity<>(headers);

        Map<String, String> pathParam = new HashMap<>();
        pathParam.put("encryptedPUUID", puuid);

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
