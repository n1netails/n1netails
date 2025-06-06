package com.n1netails.n1netails.api.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "organization", schema = "ntail")
public class OrganizationEntity {

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
    @ManyToMany(mappedBy = "organizations")
    @JsonIgnore
    private Set<UsersEntity> users = new HashSet<>();
}
