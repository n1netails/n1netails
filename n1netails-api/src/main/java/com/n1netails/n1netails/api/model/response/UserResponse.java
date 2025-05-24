package com.n1netails.n1netails.api.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
// import java.util.List; // Not using List<String> authorities for now, sticking to String role

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id; // Internal DB ID
    private String userId; // Public User ID
    private String firstName;
    private String lastName;
    private String email;
    private String role; // Simplified role
    private boolean active;
    private boolean notLocked;
    private Date joinDate;
}
