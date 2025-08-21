package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.exception.type.TailAlreadyBookmarkedException;
import com.n1netails.n1netails.api.exception.type.TailNotFoundException;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.entity.TailBookmarkEntity;
import com.n1netails.n1netails.api.model.entity.TailEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.repository.TailBookmarkRepository;
import com.n1netails.n1netails.api.repository.TailRepository;
import com.n1netails.n1netails.api.repository.UserRepository;
import com.n1netails.n1netails.api.service.TailBookmarkService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TailBookmarkServiceImpl implements TailBookmarkService {

    private final TailBookmarkRepository tailBookmarkRepository;
    private final UserRepository userRepository;
    private final TailRepository tailRepository;


    @Override
    @Transactional
    public TailBookmarkEntity bookmarkTail(Long userId, Long tailId) throws UserNotFoundException, TailNotFoundException, TailAlreadyBookmarkedException {
        if (tailBookmarkRepository.existsByUserIdAndTailId(userId, tailId)) {
            throw new TailAlreadyBookmarkedException("Tail " + tailId + " is already bookmarked by user " + userId);
        }
        UsersEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        TailEntity tail = tailRepository.findById(tailId)
                .orElseThrow(() -> new TailNotFoundException("Tail not found with id: " + tailId));

        TailBookmarkEntity bookmark = new TailBookmarkEntity();
        bookmark.setUser(user);
        bookmark.setTail(tail);

        return tailBookmarkRepository.save(bookmark);
    }

    @Override
    public void removeBookmark(Long userId, Long tailId) {
        tailBookmarkRepository.deleteByUserIdAndTailId(userId, tailId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TailEntity> getUserBookmarks(Long userId) {
        List<TailBookmarkEntity> bookmarks = tailBookmarkRepository.findByUserId(userId);
        return bookmarks.stream()
                .map(TailBookmarkEntity::getTail)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBookmarked(Long userId, Long tailId) {
        return tailBookmarkRepository.existsByUserIdAndTailId(userId, tailId);
    }
}
