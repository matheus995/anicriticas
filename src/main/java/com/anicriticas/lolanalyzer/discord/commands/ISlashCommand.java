package com.anicriticas.lolanalyzer.discord.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

public interface ISlashCommand {

    String getName();

    Mono<Void> handle(ChatInputInteractionEvent event);
}
