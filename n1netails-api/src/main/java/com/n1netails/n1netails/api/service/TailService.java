package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.model.core.TailLevel;
import com.n1netails.n1netails.api.model.core.TailStatus;
import com.n1netails.n1netails.api.model.core.TailType;
import com.n1netails.n1netails.api.model.request.TailRequest;
import com.n1netails.n1netails.api.model.response.TailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TailService {

    Page<TailResponse> getTails(Pageable pageable);
    TailResponse getTailById(Long id);
    TailResponse createTail(TailRequest request);
    TailResponse updateTail(Long id, TailRequest request);
    void deleteTail(Long id);

    TailResponse updateTailStatus(Long id, TailStatus tailStatus);
    TailResponse updateTailLevel(Long id, TailLevel tailLevel);
    TailResponse updateTailType(Long id, TailType tailType);
}
