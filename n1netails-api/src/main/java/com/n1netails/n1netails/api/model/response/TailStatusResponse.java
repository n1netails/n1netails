package com.n1netails.n1netails.api.model.response;

import com.n1netails.n1netails.api.model.core.TailStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TailStatusResponse extends TailStatus {

    private Long id;
}
