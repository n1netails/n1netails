package com.n1netails.n1netails.api.model.entity;

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
@Table(name = "users", schema = "ntail")
public class UsersEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    @SequenceGenerator(name = "users_seq", sequenceName = "users_seq", allocationSize = 1)
    @Column(nullable = false, updatable = false)
    private Long id;
    private String userId;
    private String provider; // used for oauth2 login ex. "GITHUB","GOOGLE"
    private String providerId; // used for oauth2 user id
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    @Column(unique = true)
    private String email;
    private boolean emailVerified = false;
    private String profileImageUrl;
    private Date lastLoginDate;
    private Date lastLoginDateDisplay;
    private Date joinDate;
    private String role; // ROLE_USER, ROLE_ADMIN
    private String[] authorities;
    private boolean isActive;
    private boolean isNotLocked;
    private boolean enabled;

    // Many-to-many relationship with Users (a user can belong to multiple organizations)
    @ManyToMany
    @JoinTable(
            name = "user_organizations",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "organization_id")
    )
    private Set<OrganizationEntity> organizations = new HashSet<>();
}
