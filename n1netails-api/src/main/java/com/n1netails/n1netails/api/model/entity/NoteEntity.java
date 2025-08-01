package com.n1netails.n1netails.api.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "note", schema = "ntail")
public class NoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "note_seq")
    @SequenceGenerator(name = "note_seq", sequenceName = "note_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    private TailEntity tail;

    @ManyToOne
    private UsersEntity user;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String content;

    private Instant createdAt;

    @ManyToOne
    private OrganizationEntity organization;

    private boolean n1;

    private boolean isHuman;

    private String llmProvider;

    private String llmModel;

}
