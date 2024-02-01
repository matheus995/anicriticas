package com.anicriticas.lolanalyzer.enums;

import com.anicriticas.lolanalyzer.exceptions.RegionNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum RegionEnum {

    BR1("Brazil", "Americas"),
    EUN1("Europe Nordic & East", "Europe"),
    EUW1("Europe West", "Europe"),
    JP1("Japan", "Asia"),
    KR("Korea", "Asia"),
    LA1("Latin America South (LAS)", "Americas"),
    LA2("Latin America North (LAN)", "Americas"),
    NA1("North America", "Americas"),
    OC1("Oceania", "Esports"),
    PH2("Philippines", "Esports"),
    RU("Russia", "Europe"),
    SG2("Singapore", "Esports"),
    TH2("Thailand", "Esports"),
    TR1("Turkey", "Europe"),
    TW2("Taiwan", "Esports"),
    VN2("Vietnam", "Esports");

    private final String regionName;
    private final String alternativeRegion;

    public static RegionEnum getByRegionName(String regionName) {
        return Arrays.stream(values())
                .filter(region -> region.getRegionName().equalsIgnoreCase(regionName))
                .findFirst()
                .orElseThrow(() -> new RegionNotFoundException("Region not found: " + regionName));
    }
}