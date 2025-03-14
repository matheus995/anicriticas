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
public class RiotAPIService {

    private final String riotToken = System.getenv("RIOT_TOKEN");

    @Autowired
    private RestTemplate restTemplate = new RestTemplate();

    public String getRiotAccountByNameAndId(String riotNickName, String riotId, Region region) {
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

    public String getRiotAccountByPuuid(String puuid, Region region) {
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
}
