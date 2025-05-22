package com.n1netails.n1netails.api.model.response;

import com.n1netails.n1netails.api.model.dto.TailType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TailTypeResponse extends TailType {

    private Long id;
}
