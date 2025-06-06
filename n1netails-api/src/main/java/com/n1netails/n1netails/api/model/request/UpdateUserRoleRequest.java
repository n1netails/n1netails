package com.n1netails.n1netails.api.model.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UpdateUserRoleRequest {

    @NotEmpty(message = "New role name cannot be empty")
    private String roleName; // e.g., "ROLE_ADMIN", "ROLE_USER"
}
