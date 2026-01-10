package com.anicriticas.emojis;

import org.json.JSONObject;

import java.net.URL;

import static com.anicriticas.utils.FileUtils.getJsonObjectFromUrl;
import static com.anicriticas.utils.FileUtils.getResourceUrlByFileName;

public class AgentsEmojis {

    public static String getEmojiByAgentName(String agentName) {
        JSONObject emojis;

        URL urlAgentsEmojiFile = getResourceUrlByFileName("emojis/agents-emojis.json");
        emojis = getJsonObjectFromUrl(urlAgentsEmojiFile);

        for (String agent : emojis.keySet()) {
            if (agent.equalsIgnoreCase(agentName)) {
                return emojis.getString(agent);
            }
        }
        return "Agent Emoji not found";
    }
}
