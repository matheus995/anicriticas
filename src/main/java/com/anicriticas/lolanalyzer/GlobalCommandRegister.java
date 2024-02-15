package com.anicriticas.lolanalyzer;

import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.RestClient;
import discord4j.rest.service.ApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.anicriticas.lolanalyzer.discord.commands.MatchCommand.lastMatchCommandRequest;
import static com.anicriticas.lolanalyzer.discord.commands.ProfileCommand.profileCommandRequest;

@Component
public class GlobalCommandRegister implements ApplicationRunner {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final RestClient client;

    public GlobalCommandRegister(RestClient client) {
        this.client = client;
    }

    @Override
    public void run(ApplicationArguments args) {
        final ApplicationService applicationService = client.getApplicationService();
        final long applicationId = client.getApplicationId().block();

        List<ApplicationCommandRequest> commands = new ArrayList<>();
        commands.add(lastMatchCommandRequest());
        commands.add(profileCommandRequest());

//        long guildId = 1195030988938551350L;

//        applicationService.bulkOverwriteGuildApplicationCommand(applicationId, guildId, commands)
        applicationService.bulkOverwriteGlobalApplicationCommand(applicationId, commands)
                .doOnNext(ignore -> LOGGER.debug("Successfully registered Global Commands"))
                .doOnError(e -> LOGGER.error("Failed to register global commands", e))
                .subscribe();
    }
}