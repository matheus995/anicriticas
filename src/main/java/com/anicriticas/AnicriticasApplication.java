package com.anicriticas;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import discord4j.rest.RestClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@SpringBootApplication
@EnableScheduling
public class AnicriticasApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(AnicriticasApplication.class)
                .build()
                .run(args);
    }

    @Bean
    public GatewayDiscordClient gatewayDiscordClient() {
//        return DiscordClientBuilder.create(System.getenv("DISCORD_TOKEN")).build()
        return DiscordClientBuilder.create(System.getenv("DISCORD_TOKEN_HMLG")).build()
                .gateway()
                .setInitialPresence(ignore -> ClientPresence.online(ClientActivity.listening("/profile /lastmatch")))
                .login()
                .block();
    }

    @Bean
    public RestClient discordRestClient(GatewayDiscordClient client) {
        return client.getRestClient();
    }

//    @Bean
//    public RestTemplate restTemplate() {
//        return new RestTemplate();
//    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                // Configura o Connection Timeout para 10 segundos
                .setConnectTimeout(Duration.ofSeconds(10))
                // Configura o Read Timeout para 30 segundos
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
    }

}
