package com.n1netails.n1netails.api.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tail_status", schema = "ntail")
public class TailStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tail_status_seq")
    @SequenceGenerator(name = "tail_status_seq", sequenceName = "tail_status_seq", allocationSize = 1)
    private Long id;

    private String name; // enum-like: "NEW", "IN_PROGRESS", "RESOLVED"
}
