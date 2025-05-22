package com.n1netails.n1netails.api.model.request;

import com.n1netails.n1netails.api.model.dto.N1neToken;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateTokenRequest extends N1neToken {

    private Long userId;
    private Long organizationId;
}
