package com.n1netails.n1netails.api.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.n1netails.n1netails.api.model.entity.OrganizationEntity;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tail", schema = "ntail")
public class TailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tail_seq")
    @SequenceGenerator(name = "tail_seq", sequenceName = "tail_seq", allocationSize = 1)
    private Long id;

    private String title;
    private String description;
    private Instant timestamp;
    private Instant resolvedTimestamp;
    private Long assignedUserId;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "TEXT")
    private String details;
    @ManyToOne
    private TailLevelEntity level;
    @ManyToOne
    private TailTypeEntity type;
    @ManyToOne
    private TailStatusEntity status;
    @ManyToMany
    private List<RunbookEntity> runbooks;
    @OneToMany(mappedBy = "tail", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoteEntity> notes;
    @OneToMany(mappedBy = "tail", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TailVariableEntity> customVariables;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private OrganizationEntity organization;
}
