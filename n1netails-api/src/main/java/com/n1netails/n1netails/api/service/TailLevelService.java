package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.TailLevelNotFoundException;
import com.n1netails.n1netails.api.model.core.TailLevel;
import com.n1netails.n1netails.api.model.request.PageRequest;
import com.n1netails.n1netails.api.model.response.TailLevelResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TailLevelService {

    Page<TailLevelResponse> getTailLevels(PageRequest request);
    TailLevelResponse getTailLevelById(Long id);
    TailLevelResponse createTailLevel(TailLevel request);
    TailLevelResponse updateTailLevel(Long id, TailLevel request);
    void deleteTailLevel(Long id) throws TailLevelNotFoundException;
}
