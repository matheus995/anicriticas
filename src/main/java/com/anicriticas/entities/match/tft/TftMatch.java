package com.anicriticas.entities.match.tft;

import com.anicriticas.entities.Player;
import com.anicriticas.entities.match.Match;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TftMatch extends Match {

    private List<Player> winners;
}
