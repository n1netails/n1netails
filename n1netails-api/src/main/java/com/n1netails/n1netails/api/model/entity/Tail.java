package com.n1netails.n1netails.api.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Tail {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tail_seq")
    @SequenceGenerator(name = "tail_seq", sequenceName = "tail_seq", allocationSize = 1)
    private Long id;

    private String title;
    private String description;
    private Instant timestamp;
    private Instant resolvedTimestamp;
    private String assignedUserId;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String details;
    @ManyToOne
    private TailLevel level;
    @ManyToOne
    private TailType type;
    @ManyToOne
    private TailStatus status;
    @ManyToMany
    private List<Runbook> runbooks;
    @OneToMany(mappedBy = "tail", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Note> notes;
    @OneToMany(mappedBy = "tail", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TailVariable> customVariables;
}
