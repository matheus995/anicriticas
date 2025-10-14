package com.anicriticas.utils;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class FileUtils {

    public static JSONObject getJsonObjectFromUrl(URL url) {
        JSONObject json;

        try {
            json = new JSONObject(IOUtils.toString(url, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return json;
    }

    public static JSONArray getJsonArrayFromUrl(URL url) {
        JSONArray jsonArray;

        try {
            jsonArray = new JSONArray(IOUtils.toString(url, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return jsonArray;
    }

    public static String getJsonFromUrl(URL url) {
        try {
            return IOUtils.toString(url, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static URL getResourceUrlByFileName(String fileName) {
        URL url = FileUtils.class.getClassLoader().getResource(fileName);

        if (Objects.isNull(url)) {
            throw new RuntimeException("File not found in resources: " + fileName);
        }

        return url;
    }

    public static InputStream getResourceStreamUrlByFileName(String fileName) {
        InputStream url = FileUtils.class.getClassLoader().getResourceAsStream(fileName);

        if (Objects.isNull(url)) {
            throw new RuntimeException("File not found in resources: " + fileName);
        }

        return url;
    }
}
