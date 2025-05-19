package com.n1netails.n1netails.api.model.entity;

import jakarta.persistence.*;

import java.time.Instant;

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
    private String content;

    private Instant createdAt;
}
