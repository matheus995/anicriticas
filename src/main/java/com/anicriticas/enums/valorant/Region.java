package com.anicriticas.enums.valorant;

import com.anicriticas.exceptions.RegionNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Region {

    BR("Brazil"),
    EU("Europe"),
    KR("Korea"),
    LA("Latam"),
    NA("North America");

    private final String regionName;

    public static Region getRegionByName(String regionName) {
        return Arrays.stream(values())
                .filter(region -> region.getRegionName().equalsIgnoreCase(regionName))
                .findFirst()
                .orElseThrow(() -> new RegionNotFoundException("Region not found: " + regionName));
    }

    public static Region getRegionByEnumName(String enumName) {
        return Arrays.stream(values())
                .filter(region -> region.name().equalsIgnoreCase(enumName))
                .findFirst()
                .orElseThrow(() -> new RegionNotFoundException("Region not found: " + enumName));
    }
}