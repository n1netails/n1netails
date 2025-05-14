package com.n1netails.n1netails.api.model.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Runbook {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "runbook_seq")
    @SequenceGenerator(name = "runbook_seq", sequenceName = "runbook_seq", allocationSize = 1)
    private Long id;

    private String title;
    @Lob
    private String steps;

    @ManyToMany(mappedBy = "runbooks")
    private List<Tail> relatedTails;

    @ManyToMany
    private List<TailType> relatedTailTypes;
}
