package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.model.dto.TailStatus;
import com.n1netails.n1netails.api.model.response.TailStatusResponse;

import java.util.List;

public interface TailStatusService {

    List<TailStatusResponse> getTailStatusList();
    TailStatusResponse getTailStatusById(Long id);
    TailStatusResponse createTailStatus(TailStatus request);
    TailStatusResponse updateTailStatus(Long id, TailStatus request);
    void deleteTailStatus(Long id);
}
