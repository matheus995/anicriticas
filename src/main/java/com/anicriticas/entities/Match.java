package com.anicriticas.entities;

import com.anicriticas.enums.QueueEnum;

import java.time.LocalDateTime;
import java.util.List;

public abstract class Match {

    private int id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private QueueEnum queueType;
    private List<Player> players;
}
