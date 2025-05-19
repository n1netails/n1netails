package com.n1netails.n1netails.api.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tail_type", schema = "ntail")
public class TailTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tail_type_seq")
    @SequenceGenerator(name = "tail_type_seq", sequenceName = "tail_type_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    private String name;
    private String description;

    @ManyToMany(mappedBy = "relatedTailTypes")
    private List<RunbookEntity> runbooks;
}
