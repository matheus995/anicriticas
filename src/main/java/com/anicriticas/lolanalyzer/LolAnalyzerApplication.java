package com.anicriticas.lolanalyzer;

import com.anicriticas.lolanalyzer.discord.Bot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class LolAnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LolAnalyzerApplication.class, args);

        Bot.startDiscordBot();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
