package com.n1ne.n1netails.api.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organization_seq")
    @SequenceGenerator(name = "organization_seq", sequenceName = "organization_seq", allocationSize = 1)
    @Column(nullable = false, updatable = false)
    private Long id;

    private String name;
    private String description;
    private String address;
    private Date createdAt;
    private Date updatedAt;

    // Many-to-many relationship with Users (a user can belong to multiple organizations)
    @ManyToMany
    @JoinTable(
            name = "user_organizations", // Join table to map users to organizations
            joinColumns = @JoinColumn(name = "organization_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<Users> users = new HashSet<>();
}
