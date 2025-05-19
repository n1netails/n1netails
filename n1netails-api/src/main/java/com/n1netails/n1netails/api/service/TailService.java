package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.model.dto.TailLevel;
import com.n1netails.n1netails.api.model.dto.TailStatus;
import com.n1netails.n1netails.api.model.dto.TailType;
import com.n1netails.n1netails.api.model.request.TailRequest;
import com.n1netails.n1netails.api.model.response.TailResponse;

import java.util.List;

public interface TailService {

    List<TailResponse> getTails();
    TailResponse getTailById(Long id);
    TailResponse createTail(TailRequest request);
    TailResponse updateTail(Long id, TailRequest request);
    void deleteTail(Long id);

    TailResponse updateTailStatus(Long id, TailStatus tailStatus);
    TailResponse updateTailLevel(Long id, TailLevel tailLevel);
    TailResponse updateTailType(Long id, TailType tailType);
}
