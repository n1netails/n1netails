package com.n1ne.n1netails.api.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class TailLevel {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String description;
}
