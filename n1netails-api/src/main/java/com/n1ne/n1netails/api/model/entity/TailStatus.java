package com.n1ne.n1netails.api.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class TailStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tail_status_seq")
    @SequenceGenerator(name = "tail_status_seq", sequenceName = "tail_status_seq", allocationSize = 1)
    private Long id;

    private String name; // enum-like: "NEW", "IN_PROGRESS", "RESOLVED"
}
