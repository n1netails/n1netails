package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.model.core.TailLevel;
import com.n1netails.n1netails.api.model.response.TailLevelResponse;

import java.util.List;

public interface TailLevelService {

    List<TailLevelResponse> getTailLevels();
    TailLevelResponse getTailLevelById(Long id);
    TailLevelResponse createTailLevel(TailLevel request);
    TailLevelResponse updateTailLevel(Long id, TailLevel request);
    void deleteTailLevel(Long id);
}
