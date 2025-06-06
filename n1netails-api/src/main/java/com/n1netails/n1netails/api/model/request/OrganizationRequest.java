package com.n1netails.n1netails.api.model.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class OrganizationRequest {

    @NotEmpty(message = "Organization name cannot be empty")
    private String name;
    private String description;
    private String address;
}
