package com.anicriticas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table (name = "notified_matches")
@AllArgsConstructor
public class NotifiedMatches implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private UUID id;

    @Column (nullable = false)
    private String date;

    @Column
    private String map;

    public NotifiedMatches() {}
}
