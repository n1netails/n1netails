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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.n1netails.n1netails.api.util.UserUtil.isInN1netailsOrg;

@Slf4j
@Service
@AllArgsConstructor
public class TailBookmarkServiceImpl implements TailBookmarkService {

    private final TailService tailService;
    private final TailBookmarkRepository tailBookmarkRepository;
    private final UserRepository userRepository;
    private final TailRepository tailRepository;
    private final TailLevelRepository levelRepository;
    private final TailTypeRepository typeRepository;
    private final TailStatusRepository statusRepository;


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


        // Authorization logic implemented. Users can only see tails related to their organizations.
        // If a user is part of the n1netails organization, they can only view their own tails.
        // UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(); // Removed
        List<Long> organizationIds = currentUser.getOrganizations().stream().map(OrganizationEntity::getId).toList();

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        String searchTerm = request.getSearchTerm() == null || request.getSearchTerm().isEmpty() ? "" : request.getSearchTerm();

        List<String> statuses;
        if (request.getFilterByStatus() == null || request.getFilterByStatus().isEmpty()) {
            List<TailStatusEntity> statusEntities = this.statusRepository.findAll();
            statuses = statusEntities.stream().map(TailStatusEntity::getName).toList();
        } else {
            TailStatusEntity tailStatusEntity = this.statusRepository.findTailStatusByName(request.getFilterByStatus())
                    .orElseThrow(() -> new TailStatusNotFoundException("Requested status name does not exist."));
            statuses = List.of(tailStatusEntity.getName());
        }
        log.info("STATUS NAMES: {}", statuses);

        List<String> types;
        if (request.getFilterByType() == null || request.getFilterByType().isEmpty()) {
            List<TailTypeEntity> tailTypeEntities = this.typeRepository.findAll();
            types = tailTypeEntities.stream().map(TailTypeEntity::getName).toList();
        } else {
            TailTypeEntity tailTypeEntity = this.typeRepository.findTailTypeByName(request.getFilterByType())
                    .orElseThrow(() -> new TailTypeNotFoundException("Requested type name does not exist."));
            types = List.of(tailTypeEntity.getName());
        }
        log.info("TYPE NAMES: {}", types);

        List<String> levels;
        if (request.getFilterByLevel() == null || request.getFilterByLevel().isEmpty()) {
            List<TailLevelEntity> tailLevelEntities = this.levelRepository.findAll();
            levels = tailLevelEntities.stream().map(TailLevelEntity::getName).toList();
        } else {
            TailLevelEntity tailLevelEntity = this.levelRepository.findTailLevelByName(request.getFilterByLevel())
                    .orElseThrow(() -> new TailLevelNotFoundException("Requested level name does not exist."));
            levels = List.of(tailLevelEntity.getName());
        }
        log.info("LEVEL NAMES: {}", levels);

//        boolean isN1netails = isInN1netailsOrg(currentUser);

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

        return tailBookmarkPage.map(this::setTailSummaryResponse);



        //        // todo handle page request
//        // load tails by search filter then only display bookmarked tails
//
//        // todo this need to go first before this.tailService.getTails
//        List<TailBookmarkEntity> bookmarks = tailBookmarkRepository.findByUserId(currentUser.getId());
//
//        List<TailEntity> bookmarksTailEntity = bookmarks.stream()
//                .map(TailBookmarkEntity::getTail)
//                .toList();
//        List<TailResponse> bookmarksTailResponse = bookmarksTailEntity.stream()
//                .map(this::toTailResponse)
//                .toList();
//
//
//        Page<TailResponse> pagedTails = this.tailService.getTails(request, currentUser);
//
//        // todo implement filter to remove data that is not bookmarked
//
////        Page<TailResponse> bookmarkedPagedTails = pagedTails.stream()
////                .filter(tail -> tail.getId() == )
////                .toList();
//
//        return null;
//
//        // todo filter out tails that are not bookmarked
////        return bookmarks.stream()
////                .map(TailBookmarkEntity::getTail)
////                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBookmarked(Long userId, Long tailId) {
        return tailBookmarkRepository.existsByUserIdAndTailId(userId, tailId);
    }

    private TailResponse setTailSummaryResponse(TailSummary tailSummary) {
        TailResponse tailResponse  = new TailResponse();
        tailResponse.setId(tailSummary.getId());
        tailResponse.setTitle(tailSummary.getTitle());
        tailResponse.setDescription(tailSummary.getDescription());
        tailResponse.setTimestamp(tailSummary.getTimestamp());
        tailResponse.setResolvedTimestamp(tailSummary.getResolvedTimestamp());
        tailResponse.setAssignedUserId(tailSummary.getAssignedUserId());
        UsersEntity user = userRepository.findUserById(tailSummary.getAssignedUserId());
        tailResponse.setAssignedUsername(user.getUsername());
        tailResponse.setLevel(tailSummary.getLevel());
        tailResponse.setType(tailSummary.getType());
        tailResponse.setStatus(tailSummary.getStatus());
        return tailResponse;
    }

    // todo use this to set tail response
//    private TailResponse toTailResponse(TailEntity tailEntity) {
//        return new TailResponse(
//                tailEntity.getId(),
//                tailEntity.getTitle(),
//                tailEntity.getDescription(),
//                tailEntity.getTimestamp(),
//                tailEntity.getResolvedTimestamp(),
//                tailEntity.getAssignedUserId(),
//                null, // assignedUsername is not in TailEntity, so I'll leave it null
//                tailEntity.getDetails(),
//                tailEntity.getLevel() != null ? tailEntity.getLevel().getName() : null,
//                tailEntity.getType() != null ? tailEntity.getType().getName() : null,
//                tailEntity.getStatus() != null ? tailEntity.getStatus().getName() : null,
//                null, // metadata is not in TailEntity
//                tailEntity.getOrganization() != null ? tailEntity.getOrganization().getId() : null
//        );
//    }
}
