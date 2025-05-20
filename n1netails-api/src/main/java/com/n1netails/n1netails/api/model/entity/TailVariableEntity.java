package com.n1netails.n1netails.api.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tail_variable", schema = "ntail")
public class TailVariableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tail_variable_seq")
    @SequenceGenerator(name = "tail_variable_seq", sequenceName = "tail_variable_seq", allocationSize = 1)
    private Long id;

    private String key;
    private String value;

    @ManyToOne
    private TailEntity tail;
}
