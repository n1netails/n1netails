package com.n1ne.n1netails.api.model.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class Note {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Tail tail;

    // TODO add value to track user

    @Lob
    private String content;

    private Instant createdAt;
}
