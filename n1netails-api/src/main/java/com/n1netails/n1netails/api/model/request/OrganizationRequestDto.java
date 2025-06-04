package com.n1netails.n1netails.api.model.request;

import lombok.Data;
import jakarta.validation.constraints.NotEmpty;

@Data
public class OrganizationRequestDto {
    @NotEmpty(message = "Organization name cannot be empty")
    private String name;
    private String description;
    private String address;
}
