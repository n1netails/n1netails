package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.TailTypeNotFoundException;
import com.n1netails.n1netails.api.model.core.TailType;
import com.n1netails.n1netails.api.model.response.TailTypeResponse;

import java.util.List;

public interface TailTypeService {

    List<TailTypeResponse> getTailTypes();
    TailTypeResponse getTailTypeById(Long id);
    TailTypeResponse createTailType(TailType request);
    TailTypeResponse updateTailType(Long id, TailType request);
    void deleteTailType(Long id) throws TailTypeNotFoundException;
}
