package com.anicriticas.endpoints;

import com.anicriticas.enums.Region;

public class General {

    public static String getRegionBaseUrl(Region region) {
        return "https://" + region.name().toLowerCase() + ".api.riotgames.com";
    }

    public static String getAlternativeRegionBaseUrl(Region region) {
        return "https://" + region.getAlternativeRegion().toLowerCase() + ".api.riotgames.com";
    }
}
