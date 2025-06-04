package com.n1netails.n1netails.api.model.request;

import lombok.Data;
import jakarta.validation.constraints.NotEmpty;

@Data
public class UpdateUserRoleRequestDto {
    @NotEmpty(message = "New role name cannot be empty")
    private String roleName; // e.g., "ROLE_ADMIN", "ROLE_USER"
}
