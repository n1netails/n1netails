package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.*;
import com.n1netails.n1netails.api.model.core.TailStatus;
import com.n1netails.n1netails.api.model.dto.TailSummary;
import com.n1netails.n1netails.api.model.request.ResolveTailRequest;
import com.n1netails.n1netails.api.model.request.TailPageRequest;
import com.n1netails.n1netails.api.model.response.TailResponse;
import com.n1netails.n1netails.api.model.UserPrincipal; // Added import
import org.springframework.data.domain.Page;

import java.util.List;

public interface TailService {

    Page<TailResponse> getTails(TailPageRequest request, UserPrincipal currentUser) throws TailStatusNotFoundException, TailTypeNotFoundException, TailLevelNotFoundException;

    List<String> getTailLevels(TailPageRequest request) throws TailLevelNotFoundException;
    List<String> getTailTypes(TailPageRequest request) throws TailTypeNotFoundException;
    List<String> getTailStatuses(TailPageRequest request) throws TailStatusNotFoundException;

    List<TailResponse> getTop9NewestTails(UserPrincipal currentUser);
    TailResponse getTailById(Long id, UserPrincipal currentUser) throws TailNotFoundException, UnauthorizedException;

    TailResponse updateStatus(Long id, TailStatus tailStatus, UserPrincipal currentUser) throws TailNotFoundException, UnauthorizedException;

    void markResolved(ResolveTailRequest request, UserPrincipal currentUser) throws TailNotFoundException, TailStatusNotFoundException, UnauthorizedException;

    TailResponse setTailSummaryResponse(TailSummary tailSummary);
}
