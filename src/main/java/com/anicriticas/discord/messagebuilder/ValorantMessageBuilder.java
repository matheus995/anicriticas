package com.anicriticas.discord.messagebuilder;

import com.anicriticas.entities.valorant.Match;
import com.anicriticas.model.Players;
import com.anicriticas.utils.MatchUtils;
import discord4j.core.spec.EmbedCreateFields;

import static com.anicriticas.emojis.AgentsEmojis.getEmojiByAgentName;
import static com.anicriticas.emojis.ValorantElosEmojis.getEmojiByElo;

public class ValorantMessageBuilder {

    public static EmbedCreateFields.Field getMatchInformation(Match lastMatch, String matchResult) {
        String[] matchInformation = {String.format("`Map:` %s", lastMatch.getMap()),
                String.format("`Score:` %s", MatchUtils.getValorantFinishedMatchScore(lastMatch, matchResult)),
                String.format("`Date:` %s", lastMatch.getStartDate()),
                String.format("`Duration:` %s", lastMatch.getDuration())};

        return EmbedCreateFields.Field.of("> Match information", String.join("\n", matchInformation), false);
    }

    public static EmbedCreateFields.Field getMatchPlayersKdaByTeam(Match match, String team) {
        StringBuilder teamParticipants = new StringBuilder();

        for (Players player : match.getPlayers()) {
            if (player.getTeam().equalsIgnoreCase(team)) {
                String formattedTotalSummonerStats = String.format("(**%s**/**%s**/**%s**)"
                        , player.getPlayerStats().getKills()
                        , player.getPlayerStats().getDeaths()
                        , player.getPlayerStats().getAssists());

                teamParticipants.append(formatPlayerKdaToFitInOneLine(getEmojiByElo(player.getRanking()), getEmojiByAgentName(player.getAgent()), player.getName(), formattedTotalSummonerStats));
            }
        }

        return EmbedCreateFields.Field.of("> " + team + " team", String.join("", teamParticipants), true);
    }

    private static String formatPlayerKdaToFitInOneLine(String rankEmoji, String agentEmoji, String playerName, String playerStats) {
        String stringWithoutEmoji = String.format("%s | %s\n", playerName, playerStats);

        if (stringWithoutEmoji.length() >= 45) {
            playerName = playerName.substring(0, 6) + ".";
        }

        return String.format("%s %s %s | %s\n", rankEmoji, agentEmoji, playerName, playerStats);
    }
}
