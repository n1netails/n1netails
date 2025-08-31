package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.exception.type.*;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.dto.TailSummary;
import com.n1netails.n1netails.api.model.entity.*;
import com.n1netails.n1netails.api.model.request.TailPageRequest;
import com.n1netails.n1netails.api.model.response.TailResponse;
import com.n1netails.n1netails.api.repository.*;
import com.n1netails.n1netails.api.service.TailBookmarkService;
import com.n1netails.n1netails.api.service.TailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class TailBookmarkServiceImpl implements TailBookmarkService {

    private final TailService tailService;
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
    public Page<TailResponse> getUserBookmarks(TailPageRequest request, UserPrincipal currentUser) throws TailTypeNotFoundException, TailLevelNotFoundException, TailStatusNotFoundException {

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        String searchTerm = request.getSearchTerm() == null || request.getSearchTerm().isEmpty() ? "" : request.getSearchTerm();

        List<String> statuses = tailService.getTailStatuses(request);
        List<String> types = tailService.getTailTypes(request);
        List<String> levels = tailService.getTailLevels(request);

        Page<TailSummary> tailBookmarkPage;

        if (currentUser.getId() == null) {
            // Handle case where user ID is null for n1netails user, perhaps throw exception or return empty page
            log.warn("User ID is null for n1netails user: {}", currentUser.getUsername());
            return Page.empty(pageable);
        }

        tailBookmarkPage = tailBookmarkRepository.findAllBookmarksBySearchTermAndTailFilters(
          searchTerm,
          statuses,
          types,
          levels,
          currentUser.getId(),
          pageable
        );

        return tailBookmarkPage.map(tailService::setTailSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBookmarked(Long userId, Long tailId) {
        return tailBookmarkRepository.existsByUserIdAndTailId(userId, tailId);
    }
}
