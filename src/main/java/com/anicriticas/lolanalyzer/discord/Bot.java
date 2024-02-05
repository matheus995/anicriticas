package com.anicriticas.lolanalyzer.discord;

import com.anicriticas.lolanalyzer.discord.commands.MatchCommand;
import com.anicriticas.lolanalyzer.discord.commands.ProfileCommand;
import com.anicriticas.lolanalyzer.discord.options.PlayerIdentifierOptions;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.EnumSet;

public class Bot extends ListenerAdapter {

    public static JDA jda;

    public static void startDiscordBot() {
        EnumSet<GatewayIntent> intents = EnumSet.of(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT
        );

        try {
            jda = JDABuilder.create(System.getenv("DISCORD_TOKEN"), intents)
                    .build()
                    .awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        jda.addEventListener(new MatchCommand(), new ProfileCommand());

//        Guild guild = jda.getGuildById("1195030988938551350"); // Anicriticas
//        assert guild != null;

        jda.upsertCommand("lastmatch", "Retrieve data from last match")
                .addOptions(
                        PlayerIdentifierOptions.getPlayerIdentifierOptions()
                )
                .queue();

        jda.upsertCommand("profile", "Retrieve profile data from a summoner")
                .addOptions(
                        PlayerIdentifierOptions.getPlayerIdentifierOptions()
                )
                .queue();
    }
}
