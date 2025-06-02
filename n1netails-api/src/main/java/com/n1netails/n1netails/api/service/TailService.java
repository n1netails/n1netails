package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.TailLevelNotFoundException;
import com.n1netails.n1netails.api.exception.type.TailNotFoundException;
import com.n1netails.n1netails.api.exception.type.TailStatusNotFoundException;
import com.n1netails.n1netails.api.exception.type.TailTypeNotFoundException;
import com.n1netails.n1netails.api.model.core.TailLevel;
import com.n1netails.n1netails.api.model.core.TailStatus;
import com.n1netails.n1netails.api.model.core.TailType;
import com.n1netails.n1netails.api.model.request.ResolveTailRequest;
import com.n1netails.n1netails.api.model.request.TailPageRequest;
import com.n1netails.n1netails.api.model.request.TailRequest;
import com.n1netails.n1netails.api.model.response.TailResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TailService {

    Page<TailResponse> getTails(TailPageRequest request) throws TailStatusNotFoundException, TailTypeNotFoundException, TailLevelNotFoundException;
    List<TailResponse> getTop9NewestTails();

    List<TailResponse> getTails();
    TailResponse getTailById(Long id);
    TailResponse createTail(TailRequest request);
    TailResponse updateTail(Long id, TailRequest request);
    void deleteTail(Long id);

    TailResponse updateTailStatus(Long id, TailStatus tailStatus);
    TailResponse updateTailLevel(Long id, TailLevel tailLevel);
    TailResponse updateTailType(Long id, TailType tailType);

    void markResolved(ResolveTailRequest request) throws TailNotFoundException, TailStatusNotFoundException;
}
