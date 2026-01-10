package com.anicriticas.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.io.Serializable;

@Entity
@Getter
@Table (name = "regions")
public class Regions implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false, length = 4)
    private String code;

    @Column (nullable = false, length = 100)
    private String name;
}
