package com.n1netails.n1netails.api.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class TailLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tail_level_seq")
    @SequenceGenerator(name = "tail_level_seq", sequenceName = "tail_level_seq", allocationSize = 1)
    private Long id;

    private String name;
    private String description;
}
