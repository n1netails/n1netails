package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.model.dto.TailLevelDto;
import com.n1netails.n1netails.api.model.dto.TailStatusDto;
import com.n1netails.n1netails.api.model.dto.TailTypeDto;
import com.n1netails.n1netails.api.model.request.TailRequest;
import com.n1netails.n1netails.api.model.response.TailResponse;

import java.util.List;

public interface TailService {

    List<TailResponse> getTails();
    TailResponse getTailById(Long id);
    TailResponse createTail(TailRequest request);
    TailResponse updateTail(Long id, TailRequest request);
    void deleteTail(Long id);

    TailResponse updateTailStatus(Long id, TailStatusDto tailStatus);
    TailResponse updateTailLevel(Long id, TailLevelDto tailLevel);
    TailResponse updateTailType(Long id, TailTypeDto tailType);
}
