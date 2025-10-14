package com.anicriticas.entities.match;

import com.anicriticas.enums.QueueEnum;
import com.anicriticas.enums.Region;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.anicriticas.utils.MatchUtils.getGameType;
import static com.anicriticas.utils.MatchUtils.getMatchDate;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Match {

    private long id;
    private String startDate;
    private String endDate;
    private QueueEnum queueType;
    private String gameMode;
    private Region region;
    private List<Participant> participants;

    @JsonProperty("gameId")
    public void setId(long id) {
        this.id = id;
    }

    @JsonProperty("gameStartTime")
    public void setStartDate(Long startDate) {
        this.startDate = getMatchDate(startDate);
    }

    @JsonProperty("gameQueueConfigId")
    public void setQueueType(int queueId) {
        this.queueType = getGameType(queueId);
    }

    @JsonProperty("platformId")
    public void setRegion(String platformId) {
        this.region = Region.getRegionByEnumName(platformId);
    }

}
