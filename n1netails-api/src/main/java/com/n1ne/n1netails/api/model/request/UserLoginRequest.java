package com.n1ne.n1netails.api.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequest {

    private String email;
    private String password;
}
