package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.TailStatusNotFoundException;
import com.n1netails.n1netails.api.model.core.TailStatus;
import com.n1netails.n1netails.api.model.request.PageRequest;
import com.n1netails.n1netails.api.model.response.TailStatusResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TailStatusService {

    Page<TailStatusResponse> getTailStatusList(PageRequest request);
    TailStatusResponse getTailStatusById(Long id);
    TailStatusResponse createTailStatus(TailStatus request);
    TailStatusResponse updateTailStatus(Long id, TailStatus request);
    void deleteTailStatus(Long id) throws TailStatusNotFoundException;
}
