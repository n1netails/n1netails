package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.*;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.entity.TailBookmarkEntity;
import com.n1netails.n1netails.api.model.entity.TailEntity;
import com.n1netails.n1netails.api.model.request.TailPageRequest;
import com.n1netails.n1netails.api.model.response.TailResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TailBookmarkService {

    TailBookmarkEntity bookmarkTail(Long userId, Long tailId) throws UserNotFoundException, TailNotFoundException, TailAlreadyBookmarkedException;

    void removeBookmark(Long userId, Long tailId);

    Page<TailResponse> getUserBookmarks(TailPageRequest request, UserPrincipal currentUser) throws TailTypeNotFoundException, TailLevelNotFoundException, TailStatusNotFoundException;

    boolean isBookmarked(Long userId, Long tailId);
}
