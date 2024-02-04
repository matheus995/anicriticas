package com.anicriticas.lolanalyzer.controller;

import com.anicriticas.lolanalyzer.enums.RegionEnum;
import com.anicriticas.lolanalyzer.service.LolAPIService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api")
public class LolAPIController {

    @Autowired
    private LolAPIService lolAPIService;

//    @GetMapping("/riot-account/{riotNickName}")
//    public ResponseEntity<String> getRiotAccountByNameAndId(@PathVariable String riotNickName, @RequestParam String riotId, @RequestParam String region) {
//        String summoner = lolAPIService.getRiotAccountByNameAndId(riotNickName, riotId, RegionEnum.getByRegionName(region));
//        return ResponseEntity.ok(summoner);
//    }
//
//    @GetMapping("/summoner/by-puuid/{summonerPuuid}")
//    public ResponseEntity<String> getSummonerByPuuid(@PathVariable String summonerPuuid, @RequestParam String region) {
//        String summoner = lolAPIService.getSummonerByPuuid(summonerPuuid, RegionEnum.getByRegionName(region));
//        return ResponseEntity.ok(summoner);
//    }
//
//    @GetMapping("/summoner/{summonerName}")
//    public ResponseEntity<String> getSummonerByName(@PathVariable String summonerName, @RequestParam String region) {
//        String summoner = lolAPIService.getSummonerByName(summonerName, RegionEnum.getByRegionName(region));
//        return ResponseEntity.ok(summoner);
//    }
//
//    @GetMapping("/match/{matchId}")
//    public ResponseEntity<String> getMatchById(@PathVariable String matchId, @RequestParam String region) {
//        JSONObject match = lolAPIService.getMatchById(matchId, RegionEnum.getByRegionName(region));
//        return ResponseEntity.ok(match.toString(4));
//    }
//
//    @GetMapping("/matches/{puuid}")
//    public ResponseEntity<String> getLastXMatchesBySummonerPuuid(@PathVariable String puuid, @RequestParam String count, @RequestParam String region) {
//        String[] match = lolAPIService.getLastMatchesIdsBySummonerPuuid(puuid, count, RegionEnum.getByRegionName(region));
//        return ResponseEntity.ok(Arrays.stream(match).toList().toString());
//    }
//
//    @GetMapping("/champions-masteries/{puuid}/top")
//    public ResponseEntity<String> getTopChampionMasteryBySummonerPuuid(@PathVariable String puuid, @RequestParam String region) {
//        JSONArray topChampionsMastery = lolAPIService.getTopChampionsMasteryBySummonerPuuid(puuid, RegionEnum.getByRegionName(region));
//        return ResponseEntity.ok(topChampionsMastery.toString(4));
//    }
//
//    @GetMapping("/ranked-stats/{summonerId}")
//    public ResponseEntity<String> getRankedStatsBySummonerId(@PathVariable String summonerId, @RequestParam String region) {
//        JSONArray rankedStats = lolAPIService.getRankedStats(summonerId, RegionEnum.getByRegionName(region));
//        return ResponseEntity.ok(rankedStats.toString(4));
//    }
}
