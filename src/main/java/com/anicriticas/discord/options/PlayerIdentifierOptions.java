package com.anicriticas.discord.options;

import com.anicriticas.enums.Region;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import discord4j.discordjson.json.ApplicationCommandOptionData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerIdentifierOptions {

    public static final String riotNickNameOption = "nickname";
    public static final String riotIdOption = "riot-id";
    public static final String regionOption = "region";

    public static List<ApplicationCommandOptionData> getPlayerIdentifierOptions() {
        List<ApplicationCommandOptionData> playerIdentifiesOptions = new ArrayList<>();

        ApplicationCommandOptionData nickNameOption = ApplicationCommandOptionData.builder()
                .name(PlayerIdentifierOptions.riotNickNameOption)
                .description("Riot nick name")
                .type(ApplicationCommandOption.Type.STRING.getValue())
                .required(true)
                .build();

        ApplicationCommandOptionData riotIdOption = ApplicationCommandOptionData.builder()
                .name(PlayerIdentifierOptions.riotIdOption)
                .description("Riot Identifier Ex. BR1")
                .type(ApplicationCommandOption.Type.STRING.getValue())
                .required(true)
                .build();

        List<ApplicationCommandOptionChoiceData> regionChoices = Arrays.asList(
                ApplicationCommandOptionChoiceData.builder().name(Region.BR1.getRegionName()).value(Region.BR1.getRegionName()).build(),
                ApplicationCommandOptionChoiceData.builder().name(Region.NA1.getRegionName()).value(Region.NA1.getRegionName()).build(),
                ApplicationCommandOptionChoiceData.builder().name(Region.EUN1.getRegionName()).value(Region.EUN1.getRegionName()).build(),
                ApplicationCommandOptionChoiceData.builder().name(Region.EUW1.getRegionName()).value(Region.EUW1.getRegionName()).build(),
                ApplicationCommandOptionChoiceData.builder().name(Region.JP1.getRegionName()).value(Region.JP1.getRegionName()).build(),
                ApplicationCommandOptionChoiceData.builder().name(Region.KR.getRegionName()).value(Region.KR.getRegionName()).build(),
                ApplicationCommandOptionChoiceData.builder().name(Region.LA1.getRegionName()).value(Region.LA1.getRegionName()).build(),
                ApplicationCommandOptionChoiceData.builder().name(Region.LA2.getRegionName()).value(Region.LA2.getRegionName()).build(),
                ApplicationCommandOptionChoiceData.builder().name(Region.OC1.getRegionName()).value(Region.OC1.getRegionName()).build(),
                ApplicationCommandOptionChoiceData.builder().name(Region.PH2.getRegionName()).value(Region.PH2.getRegionName()).build(),
                ApplicationCommandOptionChoiceData.builder().name(Region.RU.getRegionName()).value(Region.RU.getRegionName()).build(),
                ApplicationCommandOptionChoiceData.builder().name(Region.SG2.getRegionName()).value(Region.SG2.getRegionName()).build(),
                ApplicationCommandOptionChoiceData.builder().name(Region.TH2.getRegionName()).value(Region.TH2.getRegionName()).build(),
                ApplicationCommandOptionChoiceData.builder().name(Region.TR1.getRegionName()).value(Region.TR1.getRegionName()).build(),
                ApplicationCommandOptionChoiceData.builder().name(Region.TW2.getRegionName()).value(Region.TW2.getRegionName()).build(),
                ApplicationCommandOptionChoiceData.builder().name(Region.VN2.getRegionName()).value(Region.VN2.getRegionName()).build()
        );

        ApplicationCommandOptionData regionOption = ApplicationCommandOptionData.builder()
                .name(PlayerIdentifierOptions.regionOption)
                .description("Riot account region")
                .type(ApplicationCommandOption.Type.STRING.getValue())
                .choices(regionChoices)
                .required(true)
                .build();

        playerIdentifiesOptions.add(nickNameOption);
        playerIdentifiesOptions.add(riotIdOption);
        playerIdentifiesOptions.add(regionOption);

        return playerIdentifiesOptions;
    }
}
