package com.anicriticas.lolanalyzer.utils;

public class RiotAccountUtils {
    public static String removeHashTagIfExists(String riotId) {
        if (riotId.startsWith("#")) {
            return riotId.substring(1);
        }

        return riotId;
    }
}
