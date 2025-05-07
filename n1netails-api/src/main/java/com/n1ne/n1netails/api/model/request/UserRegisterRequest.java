package com.n1ne.n1netails.api.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterRequest {

    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String username;
}
