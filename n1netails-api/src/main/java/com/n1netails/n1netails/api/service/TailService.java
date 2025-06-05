package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.TailLevelNotFoundException;
import com.n1netails.n1netails.api.exception.type.TailNotFoundException;
import com.n1netails.n1netails.api.exception.type.TailStatusNotFoundException;
import com.n1netails.n1netails.api.exception.type.TailTypeNotFoundException;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.core.TailStatus;
import com.n1netails.n1netails.api.model.request.ResolveTailRequest;
import com.n1netails.n1netails.api.model.request.TailPageRequest;
import com.n1netails.n1netails.api.model.response.TailResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TailService {

    Page<TailResponse> getTails(TailPageRequest request, UserPrincipal currentUser) throws TailStatusNotFoundException, TailTypeNotFoundException, TailLevelNotFoundException;
    List<TailResponse> getTop9NewestTails(UserPrincipal currentUser);
    TailResponse getTailById(Long id);

    TailResponse updateStatus(Long id, TailStatus tailStatus);

    void markResolved(ResolveTailRequest request) throws TailNotFoundException, TailStatusNotFoundException;
}
