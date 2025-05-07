package com.n1ne.n1netails.api.model.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Runbook {

    @Id
    @GeneratedValue
    private Long id;

    private String title;
    @Lob
    private String steps;

    @ManyToMany(mappedBy = "runbooks")
    private List<Tail> relatedTails;

    @ManyToMany
    private List<TailType> relatedTailTypes;
}
