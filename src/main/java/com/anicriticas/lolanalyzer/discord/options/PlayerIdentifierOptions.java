package com.anicriticas.lolanalyzer.discord.options;

import com.anicriticas.lolanalyzer.enums.RegionEnum;
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
                ApplicationCommandOptionChoiceData.builder().name(RegionEnum.BR1.getRegionName()).value(RegionEnum.BR1.getRegionName()).build(),
                ApplicationCommandOptionChoiceData.builder().name(RegionEnum.NA1.getRegionName()).value(RegionEnum.NA1.getRegionName()).build(),
                ApplicationCommandOptionChoiceData.builder().name(RegionEnum.EUN1.getRegionName()).value(RegionEnum.EUN1.getRegionName()).build(),
                ApplicationCommandOptionChoiceData.builder().name(RegionEnum.EUW1.getRegionName()).value(RegionEnum.EUW1.getRegionName()).build(),
                ApplicationCommandOptionChoiceData.builder().name(RegionEnum.JP1.getRegionName()).value(RegionEnum.JP1.getRegionName()).build(),
                ApplicationCommandOptionChoiceData.builder().name(RegionEnum.KR.getRegionName()).value(RegionEnum.KR.getRegionName()).build(),
                ApplicationCommandOptionChoiceData.builder().name(RegionEnum.LA1.getRegionName()).value(RegionEnum.LA1.getRegionName()).build(),
                ApplicationCommandOptionChoiceData.builder().name(RegionEnum.LA2.getRegionName()).value(RegionEnum.LA2.getRegionName()).build(),
                ApplicationCommandOptionChoiceData.builder().name(RegionEnum.OC1.getRegionName()).value(RegionEnum.OC1.getRegionName()).build(),
                ApplicationCommandOptionChoiceData.builder().name(RegionEnum.PH2.getRegionName()).value(RegionEnum.PH2.getRegionName()).build(),
                ApplicationCommandOptionChoiceData.builder().name(RegionEnum.RU.getRegionName()).value(RegionEnum.RU.getRegionName()).build(),
                ApplicationCommandOptionChoiceData.builder().name(RegionEnum.SG2.getRegionName()).value(RegionEnum.SG2.getRegionName()).build(),
                ApplicationCommandOptionChoiceData.builder().name(RegionEnum.TH2.getRegionName()).value(RegionEnum.TH2.getRegionName()).build(),
                ApplicationCommandOptionChoiceData.builder().name(RegionEnum.TR1.getRegionName()).value(RegionEnum.TR1.getRegionName()).build(),
                ApplicationCommandOptionChoiceData.builder().name(RegionEnum.TW2.getRegionName()).value(RegionEnum.TW2.getRegionName()).build(),
                ApplicationCommandOptionChoiceData.builder().name(RegionEnum.VN2.getRegionName()).value(RegionEnum.VN2.getRegionName()).build()
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
