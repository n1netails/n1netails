package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.TailAlreadyBookmarkedException;
import com.n1netails.n1netails.api.exception.type.TailNotFoundException;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.entity.TailBookmarkEntity;
import com.n1netails.n1netails.api.model.entity.TailEntity;

import java.util.List;

public interface TailBookmarkService {

    TailBookmarkEntity bookmarkTail(Long userId, Long tailId) throws UserNotFoundException, TailNotFoundException, TailAlreadyBookmarkedException;

    void removeBookmark(Long userId, Long tailId);

    List<TailEntity> getUserBookmarks(Long userId);

    boolean isBookmarked(Long userId, Long tailId);
}
