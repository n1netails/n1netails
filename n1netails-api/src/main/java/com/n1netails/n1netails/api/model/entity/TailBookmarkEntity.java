package com.n1netails.n1netails.api.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tail_bookmark", schema = "ntail", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "tail_id"})
})
public class TailBookmarkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tail_bookmark_seq")
    @SequenceGenerator(name = "tail_bookmark_seq", sequenceName = "tail_bookmark_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UsersEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tail_id", nullable = false)
    private TailEntity tail;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
