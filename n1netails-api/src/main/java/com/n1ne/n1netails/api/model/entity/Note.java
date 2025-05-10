package com.n1ne.n1netails.api.model.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "note_seq")
    @SequenceGenerator(name = "note_seq", sequenceName = "note_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    private Tail tail;

    // TODO add value to track user

    @Lob
    private String content;

    private Instant createdAt;
}
