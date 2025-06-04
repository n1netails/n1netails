package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.exception.type.TailLevelNotFoundException;
import com.n1netails.n1netails.api.exception.type.TailNotFoundException;
import com.n1netails.n1netails.api.exception.type.TailStatusNotFoundException;
import com.n1netails.n1netails.api.exception.type.TailTypeNotFoundException;
import com.n1netails.n1netails.api.model.core.TailLevel;
import com.n1netails.n1netails.api.model.core.TailStatus;
import com.n1netails.n1netails.api.model.core.TailType;
import com.n1netails.n1netails.api.model.dto.TailSummary;
import com.n1netails.n1netails.api.model.entity.*;
import com.n1netails.n1netails.api.model.request.ResolveTailRequest;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.request.TailPageRequest;
import com.n1netails.n1netails.api.repository.*;
import com.n1netails.n1netails.api.model.request.TailRequest;
import com.n1netails.n1netails.api.model.response.TailResponse;
import com.n1netails.n1netails.api.service.TailService;
import com.n1netails.n1netails.api.service.UserService; // Added
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException; // Added
import org.springframework.security.core.Authentication; // Added
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Added
import org.springframework.security.core.context.SecurityContextHolder; // Added
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("tailService")
public class TailServiceImpl implements TailService {

    public static final String REQUESTED_TAIL_NOT_FOUND = "Requested Tail Not Found.";

    private final TailRepository tailRepository;
    private final UserRepository usersRepository;
    private final TailLevelRepository levelRepository;
    private final TailTypeRepository typeRepository;
    private final TailStatusRepository statusRepository;
    private final NoteRepository noteRepository;
    private final UserService userService; // Added

    @Override
    public List<TailResponse> getTails() throws TailNotFoundException {
        // Refactored logic will be similar to getTails(TailPageRequest) but without pagination/filters,
        // or this method might be removed/restricted further.
        // For now, let's apply basic visibility.

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new AccessDeniedException("User not authenticated.");
        }
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        UsersEntity currentUser = userService.findUserByEmail(principal.getUsername());

        if (currentUser == null) { // Should not happen if principal is valid
            throw new TailNotFoundException("User details not found for authenticated principal.");
        }

        List<TailEntity> tailEntities;

        if (principal.getAuthorities().contains(new SimpleGrantedAuthority("user:delete"))) { // Super Admin
            tailEntities = tailRepository.findAll();
        } else {
            Set<OrganizationEntity> userOrgs = currentUser.getOrganizations();
            if (userOrgs.size() == 1 && userOrgs.iterator().next().getName().equals("n1netails")) {
                // "n1netails" only user - can only see their assigned tails.
                // This is a bit tricky for a "list all" method.
                // For now, returning only assigned. This might need further product clarification.
                tailEntities = tailRepository.findByAssignedUserId(currentUser.getId());
            } else {
                List<Long> organizationIds = userOrgs.stream().map(OrganizationEntity::getId).toList();
                if (organizationIds.isEmpty()) { // Should not happen if user is in at least one org
                    tailEntities = new ArrayList<>();
                } else {
                    // This could be very broad if a user is in many orgs.
                    // A findByOrganizationIdIn without pagination is risky.
                    // For now, this is a placeholder for a more refined strategy or removal of this method.
                    // Let's assume for now we fetch all tails for their orgs (still potentially large).
                    // This specific implementation detail might need a dedicated repository method if we keep this.
                    // For simplicity, and acknowledging risk, I'll filter from findAll. This is NOT efficient.
                    List<TailEntity> allTails = tailRepository.findAll();
                    tailEntities = allTails.stream()
                        .filter(tail -> tail.getOrganization() != null && organizationIds.contains(tail.getOrganization().getId()))
                        .toList();
                }
            }
        }

        List<TailResponse> tailResponseList = new ArrayList<>();
        tailEntities.forEach(tail -> {
            TailResponse tailResponse = setTailResponse(tail);
            tailResponseList.add(tailResponse);
        });
        return tailResponseList;
    }

    @Override
    public TailResponse getTailById(Long id) throws TailNotFoundException, AccessDeniedException {
        TailEntity tail = tailRepository.findById(id)
                .orElseThrow(() -> new TailNotFoundException(REQUESTED_TAIL_NOT_FOUND + " with ID: " + id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new AccessDeniedException("User not authenticated.");
        }
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        UsersEntity currentUser = userService.findUserByEmail(principal.getUsername());

        if (currentUser == null) {
             throw new AccessDeniedException("User details not found for authenticated principal.");
        }

        if (principal.getAuthorities().contains(new SimpleGrantedAuthority("user:delete"))) { // Super Admin
            return setTailResponse(tail);
        }

        Set<OrganizationEntity> userOrgs = currentUser.getOrganizations();
        boolean isN1netailsOnly = userOrgs.size() == 1 && userOrgs.iterator().next().getName().equals("n1netails");

        if (isN1netailsOnly) {
            if (tail.getAssignedUserId() != null && tail.getAssignedUserId().equals(currentUser.getId())) {
                return setTailResponse(tail);
            } else {
                throw new AccessDeniedException("Access denied to tail ID: " + id);
            }
        } else { // User in other orgs
            if (tail.getOrganization() != null && userOrgs.stream().anyMatch(org -> org.getId().equals(tail.getOrganization().getId()))) {
                return setTailResponse(tail);
            } else {
                 // Fallback: if tail has no org, but user is assigned (e.g. old data), allow if n1netails only user was assigned
                if (tail.getOrganization() == null && tail.getAssignedUserId() != null && tail.getAssignedUserId().equals(currentUser.getId())) {
                     // This case is less likely if all tails get an org, but could be a transition state
                     return setTailResponse(tail);
                }
                throw new AccessDeniedException("Access denied to tail ID: " + id);
            }
        }
    }

    @Override
    public TailResponse createTail(TailRequest request) {
        TailEntity tailEntity = setTail(request);
        tailEntity = tailRepository.save(tailEntity);
        return setTailResponse(tailEntity);
    }

    @Override
    public TailResponse updateTail(Long id, TailRequest request) {
        TailEntity updatedTailEntity = new TailEntity();
        Optional<TailEntity> tail = tailRepository.findById(id);
        if (tail.isPresent()) {
            updatedTailEntity = setTail(request, tail.get());
            updatedTailEntity = tailRepository.save(updatedTailEntity);
        } else {
            log.error("updateTail - " + REQUESTED_TAIL_NOT_FOUND);
        }
        return setTailResponse(updatedTailEntity);
    }

    @Override
    public void deleteTail(Long id) {
        tailRepository.deleteById(id);
    }

    @Override
    public TailResponse updateTailStatus(Long id, TailStatus tailStatus) {
        Optional<TailEntity> tail = tailRepository.findById(id);
        TailEntity updatedTailEntity = new TailEntity();
        if (tail.isPresent()) {
            updatedTailEntity = tail.get();
            Optional<TailStatusEntity> newStatus = statusRepository.findTailStatusByName(tailStatus.getName());
            if (newStatus.isPresent()) {
                updatedTailEntity.setStatus(newStatus.get());
            } else {
                TailStatusEntity createdStatus = new TailStatusEntity();
                createdStatus.setName(tailStatus.getName());
                createdStatus = statusRepository.save(createdStatus);
                updatedTailEntity.setStatus(createdStatus);
            }
            tailRepository.save(updatedTailEntity);
        } else {
            log.error("updateTailStatus - " + REQUESTED_TAIL_NOT_FOUND);
        }
        return setTailResponse(updatedTailEntity);
    }

    @Override
    public TailResponse updateTailLevel(Long id, TailLevel tailLevel) {
        if (tailLevel.getDescription().isBlank()) tailLevel.setDescription(null);

        Optional<TailEntity> tail = tailRepository.findById(id);
        TailEntity updatedTailEntity = new TailEntity();
        if (tail.isPresent()) {
            updatedTailEntity = tail.get();
            Optional<TailLevelEntity> newLevel = levelRepository.findTailLevelByName(tailLevel.getName());
            if (newLevel.isPresent()) {
                TailLevelEntity level = newLevel.get();
                level.setDescription(tailLevel.getDescription() != null ? tailLevel.getDescription() : level.getDescription());
                updatedTailEntity.setLevel(level);
            } else {
                TailLevelEntity createdLevel = new TailLevelEntity();
                createdLevel.setName(tailLevel.getName());
                createdLevel.setDescription(tailLevel.getDescription());
                createdLevel = levelRepository.save(createdLevel);
                updatedTailEntity.setLevel(createdLevel);
            }
            tailRepository.save(updatedTailEntity);
        } else {
            log.error("updateTailLevel - " + REQUESTED_TAIL_NOT_FOUND);
        }
        return setTailResponse(updatedTailEntity);
    }

    @Override
    public TailResponse updateTailType(Long id, TailType tailType) {
        if (tailType.getDescription().isBlank()) tailType.setDescription(null);

        Optional<TailEntity> tail = tailRepository.findById(id);
        TailEntity updatedTailEntity = new TailEntity();
        if (tail.isPresent()) {
            updatedTailEntity = tail.get();
            Optional<TailTypeEntity> newType = typeRepository.findTailTypeByName(tailType.getName());
            if (newType.isPresent()) {
                TailTypeEntity type = newType.get();
                type.setDescription(tailType.getDescription() != null ? tailType.getDescription() : type.getDescription());
                updatedTailEntity.setType(type);
            } else {
                TailTypeEntity createdType = new TailTypeEntity();
                createdType.setName(tailType.getName());
                createdType.setDescription(tailType.getDescription());
                createdType = typeRepository.save(createdType);
                updatedTailEntity.setType(createdType);
            }
            tailRepository.save(updatedTailEntity);
        } else {
            log.error("updateTailType - " + REQUESTED_TAIL_NOT_FOUND);
        }
        return setTailResponse(updatedTailEntity);
    }

    @Override
    public void markResolved(ResolveTailRequest request) throws TailNotFoundException, TailStatusNotFoundException {

        UsersEntity assignedUser = this.usersRepository.findUserById(request.getUserId());
        TailEntity resolvedTail = this.tailRepository.findById(request.getTailSummary().getId())
                .orElseThrow(() -> new TailNotFoundException("The requested tail does not exist."));

        // update tail status to RESOLVED
        TailStatusEntity resolvedStatus = this.statusRepository.findTailStatusByName("RESOLVED")
                .orElseThrow(() -> new TailStatusNotFoundException("The requested tail status 'RESOLVED' does not exist."));
        resolvedTail.setStatus(resolvedStatus);
        // set tail resolved timestamp
        resolvedTail.setResolvedTimestamp(Instant.now());
        // set tail assigned user id to user id in request
        resolvedTail.setAssignedUserId(assignedUser.getId());
        NoteEntity noteEntity = new NoteEntity();
        noteEntity.setTail(resolvedTail);
        noteEntity.setUser(assignedUser);
        noteEntity.setContent(request.getNote());
        noteEntity.setCreatedAt(Instant.now());
        // save note
        this.noteRepository.save(noteEntity);
        // save tail
        this.tailRepository.save(resolvedTail);
    }

    private TailEntity setTail(TailRequest request) {
        TailEntity tailEntity = new TailEntity();
        return setTail(request, tailEntity);
    }

    private TailEntity setTail(TailRequest request, TailEntity tailEntity) {
        tailEntity.setTitle(request.getTitle());
        tailEntity.setDescription(request.getDescription());
        tailEntity.setTimestamp(request.getTimestamp());
        tailEntity.setAssignedUserId(request.getAssignedUserId());
        tailEntity.setDetails(request.getDetails());

        TailLevelEntity tailLevelEntity = new TailLevelEntity();
        tailLevelEntity.setName(request.getLevel().getName());
        tailLevelEntity.setDescription(request.getLevel().getDescription());
        tailEntity.setLevel(tailLevelEntity);

        TailTypeEntity tailTypeEntity = new TailTypeEntity();
        tailTypeEntity.setName(request.getType().getName());
        tailTypeEntity.setDescription(request.getType().getDescription());
        tailEntity.setType(tailTypeEntity);

        TailStatusEntity tailStatusEntity = new TailStatusEntity();
        tailStatusEntity.setName(request.getStatus());
        tailEntity.setStatus(tailStatusEntity);

        List<TailVariableEntity> tailVariableEntityList = getTailVariableEntities(request, tailEntity);
        tailEntity.setCustomVariables(tailVariableEntityList);
        return tailEntity;
    }

    private static List<TailVariableEntity> getTailVariableEntities(TailRequest request, TailEntity tailEntity) {
        List<TailVariableEntity> tailVariableEntityList = new ArrayList<>();
        Map<String, String> metadata = request.getMetadata();
        metadata.forEach((key, value) -> {
            TailVariableEntity tailVariableEntity = new TailVariableEntity();
            tailVariableEntity.setKey(key);
            tailVariableEntity.setValue(value);
            tailVariableEntity.setTail(tailEntity);
            tailVariableEntityList.add(tailVariableEntity);
        });
        return tailVariableEntityList;
    }

    private TailResponse setTailResponse(TailEntity tailEntity) {
        TailResponse tailResponse  = new TailResponse();
        tailResponse.setId(tailEntity.getId());
        tailResponse.setTitle(tailEntity.getTitle());
        tailResponse.setDescription(tailEntity.getDescription());
        tailResponse.setTimestamp(tailEntity.getTimestamp());
        tailResponse.setResolvedTimestamp(tailEntity.getResolvedTimestamp());
        tailResponse.setAssignedUserId(tailEntity.getAssignedUserId());
        UsersEntity user = usersRepository.findUserById(tailEntity.getAssignedUserId());
        tailResponse.setAssignedUsername(user.getUsername());
        tailResponse.setDetails(tailEntity.getDetails());
        tailResponse.setLevel(tailEntity.getLevel().getName());
        tailResponse.setType(tailEntity.getType().getName());
        tailResponse.setStatus(tailEntity.getStatus().getName());
        Map<String, String> metadata = new HashMap<>();
        List<TailVariableEntity> variables = tailEntity.getCustomVariables();
        variables.forEach(variable -> {
           metadata.put(variable.getKey(), variable.getValue());
        });
        tailResponse.setMetadata(metadata);
        return tailResponse;
    }

    @Override
    public Page<TailResponse> getTails(TailPageRequest request) throws TailStatusNotFoundException, TailTypeNotFoundException, TailLevelNotFoundException, AccessDeniedException {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        String searchTerm = request.getSearchTerm() == null || request.getSearchTerm().isEmpty() ? "" : request.getSearchTerm();

        // Common logic for status, type, level filters
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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new AccessDeniedException("User not authenticated.");
        }
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        UsersEntity currentUser = userService.findUserByEmail(principal.getUsername());

        if (currentUser == null) {
            throw new AccessDeniedException("User details not found for authenticated principal.");
        }

        Page<TailSummary> tailPage;

        if (principal.getAuthorities().contains(new SimpleGrantedAuthority("user:delete"))) { // Super Admin
            tailPage = tailRepository.findAllBySearchTermAndTailFilters(
                    searchTerm, statuses, types, levels, pageable);
        } else {
            Set<OrganizationEntity> userOrgs = currentUser.getOrganizations();
            if (userOrgs.isEmpty()){ // Should not happen for a valid user
                 return Page.empty(pageable);
            }

            boolean isN1netailsOnly = userOrgs.size() == 1 && userOrgs.iterator().next().getName().equals("n1netails");

            if (isN1netailsOnly) {
                tailPage = tailRepository.findByAssignedUserIdAndSearchTermAndFilters(
                        currentUser.getId(), searchTerm, statuses, types, levels, pageable);
            } else {
                List<Long> organizationIds = userOrgs.stream().map(OrganizationEntity::getId).toList();
                tailPage = tailRepository.findByOrganizationIdInAndSearchTermAndFilters(
                        organizationIds, searchTerm, statuses, types, levels, pageable);
            }
        }
        return tailPage.map(this::setTailSummaryResponse);
    }

    @Override
    public List<TailResponse> getTop9NewestTails() throws AccessDeniedException {
        Pageable topNine = PageRequest.of(0, 9);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new AccessDeniedException("User not authenticated.");
        }
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        UsersEntity currentUser = userService.findUserByEmail(principal.getUsername());

        if (currentUser == null) {
            throw new AccessDeniedException("User details not found for authenticated principal.");
        }

        Page<TailSummary> tailPage;

        if (principal.getAuthorities().contains(new SimpleGrantedAuthority("user:delete"))) { // Super Admin
            // Super admin sees top 9 newest across all orgs, similar to original behavior but using TailSummary
             tailPage = tailRepository.findAllByOrderByTimestampDesc(topNine); // This query already returns TailSummary
        } else {
            Set<OrganizationEntity> userOrgs = currentUser.getOrganizations();
            if (userOrgs.isEmpty()){
                 return new ArrayList<>();
            }
            boolean isN1netailsOnly = userOrgs.size() == 1 && userOrgs.iterator().next().getName().equals("n1netails");

            if (isN1netailsOnly) {
                tailPage = tailRepository.findByAssignedUserIdAndResolvedTimestampIsNullOrderByTimestampDesc(
                        currentUser.getId(), topNine);
            } else {
                List<Long> organizationIds = userOrgs.stream().map(OrganizationEntity::getId).toList();
                tailPage = tailRepository.findByOrganizationIdInAndResolvedTimestampIsNullOrderByTimestampDesc(
                        organizationIds, topNine);
            }
        }

        List<TailSummary> tailSummaryList = tailPage.getContent();
        List<TailResponse> tailResponseList = new ArrayList<>();
        tailSummaryList.forEach(tail -> {
            TailResponse tailResponse = setTailSummaryResponse(tail);
            tailResponseList.add(tailResponse);
        });
        return tailResponseList;
    }

    private TailResponse setTailSummaryResponse(TailSummary tailSummary) {
        TailResponse tailResponse  = new TailResponse();
        tailResponse.setId(tailSummary.getId());
        tailResponse.setTitle(tailSummary.getTitle());
        tailResponse.setDescription(tailSummary.getDescription());
        tailResponse.setTimestamp(tailSummary.getTimestamp());
        tailResponse.setResolvedTimestamp(tailSummary.getResolvedTimestamp());
        tailResponse.setAssignedUserId(tailSummary.getAssignedUserId());
        UsersEntity user = usersRepository.findUserById(tailSummary.getAssignedUserId());
        tailResponse.setAssignedUsername(user.getUsername());
        tailResponse.setLevel(tailSummary.getLevel());
        tailResponse.setType(tailSummary.getType());
        tailResponse.setStatus(tailSummary.getStatus());
        return tailResponse;
    }
}
