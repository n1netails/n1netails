package com.n1ne.n1netails.api.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class TailType {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tail_type_seq")
    @SequenceGenerator(name = "tail_type_seq", sequenceName = "tail_type_seq", allocationSize = 1)
    private Long id;

    private String name;
    private String description;
}
