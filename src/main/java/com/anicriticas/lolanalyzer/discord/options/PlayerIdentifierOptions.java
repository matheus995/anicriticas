package com.anicriticas.lolanalyzer.discord.options;

import com.anicriticas.lolanalyzer.enums.RegionEnum;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class PlayerIdentifierOptions {

    public static final String riotNickNameOption = "nickname";
    public static final String riotIdOption = "riot-id";
    public static final String regionOption = "region";

    public static List<OptionData> getPlayerIdentifierOptions() {
        OptionData nickNameOption = new OptionData(OptionType.STRING, PlayerIdentifierOptions.riotNickNameOption, "Riot nick name", true);

        OptionData riotIdOption = new OptionData(OptionType.STRING, PlayerIdentifierOptions.riotIdOption, "Riot Identifier Ex. BR1 | Without #", true);

        OptionData regionOption = new OptionData(OptionType.STRING, PlayerIdentifierOptions.regionOption, "Region", true)
                .addChoice(RegionEnum.BR1.getRegionName(), RegionEnum.BR1.getRegionName())
                .addChoice(RegionEnum.NA1.getRegionName(), RegionEnum.NA1.getRegionName())
                .addChoice(RegionEnum.EUN1.getRegionName(), RegionEnum.EUN1.getRegionName())
                .addChoice(RegionEnum.EUW1.getRegionName(), RegionEnum.EUW1.getRegionName())
                .addChoice(RegionEnum.JP1.getRegionName(), RegionEnum.JP1.getRegionName())
                .addChoice(RegionEnum.KR.getRegionName(), RegionEnum.KR.getRegionName())
                .addChoice(RegionEnum.LA1.getRegionName(), RegionEnum.LA1.getRegionName())
                .addChoice(RegionEnum.LA2.getRegionName(), RegionEnum.LA2.getRegionName())
                .addChoice(RegionEnum.OC1.getRegionName(), RegionEnum.OC1.getRegionName())
                .addChoice(RegionEnum.PH2.getRegionName(), RegionEnum.PH2.getRegionName())
                .addChoice(RegionEnum.RU.getRegionName(), RegionEnum.RU.getRegionName())
                .addChoice(RegionEnum.SG2.getRegionName(), RegionEnum.SG2.getRegionName())
                .addChoice(RegionEnum.TH2.getRegionName(), RegionEnum.TH2.getRegionName())
                .addChoice(RegionEnum.TR1.getRegionName(), RegionEnum.TR1.getRegionName())
                .addChoice(RegionEnum.TW2.getRegionName(), RegionEnum.TW2.getRegionName())
                .addChoice(RegionEnum.VN2.getRegionName(), RegionEnum.VN2.getRegionName());

        return List.of(nickNameOption, riotIdOption, regionOption);
    }
}
