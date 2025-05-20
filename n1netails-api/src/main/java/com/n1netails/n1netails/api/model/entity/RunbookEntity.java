package com.n1netails.n1netails.api.model.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "runbook", schema = "ntail")
public class RunbookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "runbook_seq")
    @SequenceGenerator(name = "runbook_seq", sequenceName = "runbook_seq", allocationSize = 1)
    private Long id;

    private String title;
    @Lob
    private String steps;

    @ManyToMany(mappedBy = "runbooks")
    private List<TailEntity> relatedTails;

    @ManyToMany
    @JoinTable(
            name = "runbook_related_tail_types",
            joinColumns = @JoinColumn(name = "runbook_id"),
            inverseJoinColumns = @JoinColumn(name = "tail_type_id")
    )
    private List<TailTypeEntity> relatedTailTypes;
}
