package com.anicriticas.lolanalyzer.endpoints;

import com.anicriticas.lolanalyzer.enums.RegionEnum;

public class General {

    public static String getRegionBaseUrl(RegionEnum region) {
        return "https://" + region.name().toLowerCase() + ".api.riotgames.com";
    }

    public static String getAlternativeRegionBaseUrl(RegionEnum region) {
        return "https://" + region.getAlternativeRegion().toLowerCase() + ".api.riotgames.com";
    }
}
