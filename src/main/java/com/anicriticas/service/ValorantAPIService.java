package com.anicriticas.service;

import com.anicriticas.endpoints.valorant.General;
import com.anicriticas.endpoints.valorant.MatchesV4;
import com.anicriticas.entities.valorant.Match;
import com.anicriticas.enums.valorant.MatchType;
import com.anicriticas.enums.valorant.Region;
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

import java.net.URI;
import java.util.UUID;

import static com.anicriticas.utils.ExtractValorantMatchInfo.extractData;

@Service
public class ValorantAPIService {

    private final String valorantToken = System.getenv("VALORANT_TOKEN");

    @Autowired
    private RestTemplate restTemplate = new RestTemplate();

    public Match getLastMatchIdByPlayerPuuid(UUID puuid, Region region, MatchType matchType) {
        String url = General.getBaseUrl() + MatchesV4.GET_MATCHS_BY_PLAYER_PUUID;

        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", valorantToken);

        final HttpEntity<String> entity = new HttpEntity<>(headers);

        URI uri = UriComponentsBuilder.fromUriString(url)
                .queryParam("mode", matchType.getTypeName().toLowerCase())
                .buildAndExpand(region, puuid)
                .toUri();

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
            return extractData(new JSONObject(responseEntity.getBody()));
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error when trying to retrieve last match from player uuid: " + puuid + ": " + e.getMessage());
        }
    }
}
