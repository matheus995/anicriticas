package com.anicriticas.entities.match.tft;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Character {

    private String name;
    private Integer tier;
    private Integer rarity;
    private List<String> items;

    public Character(String name, Integer tier, Integer rarity, List<String> items) {
        this.name = name;
        this.tier = tier;
        this.rarity = rarity;
        this.items = items;
    }
}
