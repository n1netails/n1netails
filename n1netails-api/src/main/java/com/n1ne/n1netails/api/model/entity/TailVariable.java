package com.n1ne.n1netails.api.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class TailVariable {

    @Id
    @GeneratedValue
    private Long id;

    private String key;
    private String value;

    @ManyToOne
    private Tail tail;
}
