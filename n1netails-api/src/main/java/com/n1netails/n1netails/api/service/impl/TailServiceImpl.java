package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.exception.type.TailLevelNotFoundException;
import com.n1netails.n1netails.api.exception.type.TailNotFoundException;
import com.n1netails.n1netails.api.exception.type.TailStatusNotFoundException;
import com.n1netails.n1netails.api.exception.type.TailTypeNotFoundException;
import com.n1netails.n1netails.api.exception.type.UnauthorizedException;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.core.TailStatus;
import com.n1netails.n1netails.api.model.dto.TailSummary;
import com.n1netails.n1netails.api.model.entity.*;
import com.n1netails.n1netails.api.model.request.ResolveTailRequest;
import com.n1netails.n1netails.api.model.request.TailPageRequest;
import com.n1netails.n1netails.api.repository.*;
import com.n1netails.n1netails.api.model.response.TailResponse;
import com.n1netails.n1netails.api.service.AuthorizationService;
import com.n1netails.n1netails.api.service.TailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("tailService")
public class TailServiceImpl implements TailService {

    public static final String REQUESTED_TAIL_NOT_FOUND = "Requested Tail Not Found.";
    public static final String N1NETAILS_ORG = "n1netails";

    private final TailRepository tailRepository;
    private final UserRepository usersRepository;
    private final TailLevelRepository levelRepository;
    private final TailTypeRepository typeRepository;
    private final TailStatusRepository statusRepository;
    private final NoteRepository noteRepository;
    private final AuthorizationService authorizationService;

    @Override
    public TailResponse getTailById(Long id, UserPrincipal currentUser) throws TailNotFoundException, UnauthorizedException {
        Optional<TailEntity> tailOptional = tailRepository.findById(id);
        if (tailOptional.isEmpty()) {
            throw new TailNotFoundException(REQUESTED_TAIL_NOT_FOUND);
        }
        TailEntity tail = tailOptional.get();
        Long organizationId = tail.getOrganization().getId();

        if (!authorizationService.belongsToOrganization(currentUser, organizationId)) {
            throw new UnauthorizedException("User does not belong to the organization associated with this tail.");
        }

        boolean isN1netails = isInN1netailsOrg(currentUser);

        if (isN1netails && !authorizationService.isTailOwner(currentUser, tail.getAssignedUserId())) {
            throw new UnauthorizedException("User is not the owner of this tail.");
        }

        return setTailResponse(tail);
    }

    @Override
    public TailResponse updateStatus(ResolveTailRequest request, UserPrincipal currentUser) throws TailNotFoundException, UnauthorizedException {

        TailStatus tailStatus = new TailStatus();
        tailStatus.setName(request.getTailSummary().getStatus());
        Long id = request.getTailSummary().getId();

        // Authorization logic implemented: User must be owner or organization admin to change tail status.
        Optional<TailEntity> tailOptional = tailRepository.findById(id);
        if (tailOptional.isEmpty()) {
            throw new TailNotFoundException(REQUESTED_TAIL_NOT_FOUND);
        }
        TailEntity tail = tailOptional.get();
        // UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(); // Removed
        Long organizationId = tail.getOrganization().getId();
        Long ownerUserId = tail.getAssignedUserId(); // assignedUserId is the owner

        // Check if the user is the owner OR an organization admin for the tail's organization
        if (!(authorizationService.isSelf(currentUser, ownerUserId) ||
              authorizationService.isOrganizationAdmin(currentUser, organizationId))) {
            throw new UnauthorizedException("User is not authorized to update the status of this tail. Must be owner or organization admin.");
        }

        Optional<TailStatusEntity> newStatus = statusRepository.findTailStatusByName(tailStatus.getName());
        if (newStatus.isPresent()) {
            tail.setStatus(newStatus.get());
        } else {
            TailStatusEntity createdStatus = new TailStatusEntity();
            createdStatus.setName(tailStatus.getName());
            createdStatus = statusRepository.save(createdStatus);
            tail.setStatus(createdStatus);
        }

        if (!request.getNote().isBlank()) {
            UsersEntity resolverUser = this.usersRepository.findUserById(request.getUserId());
            NoteEntity noteEntity = new NoteEntity();
            noteEntity.setTail(tail);
            noteEntity.setUser(resolverUser); // The note should be associated with the resolverUser
            noteEntity.setContent(request.getNote());
            noteEntity.setCreatedAt(Instant.now());
            noteEntity.setHuman(true);
            noteEntity.setN1(false);
            noteEntity.setOrganization(tail.getOrganization());
            // save note
            this.noteRepository.save(noteEntity);
        }
        tailRepository.save(tail);

        return setTailResponse(tail);
    }

    @Override
    @Transactional
    public void resolveAll(UserPrincipal currentUser) throws TailStatusNotFoundException {
        log.info("Attempting to resolve all tails");
        TailStatusEntity newStatus = statusRepository.findTailStatusByName("NEW")
                .orElseThrow(() -> new TailStatusNotFoundException("The requested tail status 'NEW' does not exist."));

        List<TailEntity> newTails = tailRepository.findAllByAssignedUserIdAndStatus(currentUser.getId(), newStatus);

        TailStatusEntity resolvedStatus = statusRepository.findTailStatusByName("RESOLVED")
                .orElseThrow(() -> new TailStatusNotFoundException("The requested tail status 'RESOLVED' does not exist."));

        Instant now = Instant.now();
        newTails.forEach(tail -> {
            tail.setStatus(resolvedStatus);
            tail.setResolvedTimestamp(now);
        });

        tailRepository.saveAll(newTails);
        log.info("Resolved {} tails for user {}", newTails.size(), currentUser.getUsername());
    }

    @Override
    public long countNewTails(UserPrincipal currentUser) {
        return tailRepository.countByAssignedUserIdAndStatusName(currentUser.getId(), "NEW");
    }

    @Override
    public void markResolved(ResolveTailRequest request, UserPrincipal currentUser) throws TailNotFoundException, TailStatusNotFoundException, UnauthorizedException { // Added currentUser
        // Authorization logic implemented: User must be the assigned user or an organization admin to mark tail as resolved.
        TailEntity tail = this.tailRepository.findById(request.getTailSummary().getId())
                .orElseThrow(() -> new TailNotFoundException("The requested tail does not exist."));

        // UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(); // Removed
        Long organizationId = tail.getOrganization().getId();
        Long assignedUserIdFromTail = tail.getAssignedUserId();

        // Check if the current user is the one to whom the tail is assigned OR an organization admin.
        // Note: request.getUserId() is the user who will be set as the resolver,
        // currentUser.getId() is the user performing the action.
        if (!(authorizationService.isSelf(currentUser, assignedUserIdFromTail) ||
              authorizationService.isOrganizationAdmin(currentUser, organizationId))) {
            throw new UnauthorizedException("User is not authorized to mark this tail as resolved. Must be the assigned user or an organization admin.");
        }

        // The user ID from the request is the user to be marked as the resolver.
        // This doesn't necessarily have to be the currentUser if an admin is resolving it on someone's behalf,
        // but the currentUser must have permission (checked above).
        UsersEntity resolverUser = this.usersRepository.findUserById(request.getUserId());
        if (resolverUser == null) {
            // Or handle this as a specific exception, e.g., UserNotFoundException
            throw new RuntimeException("Resolver user not found with ID: " + request.getUserId());
        }

        // update tail status to RESOLVED
        TailStatusEntity resolvedStatus = this.statusRepository.findTailStatusByName("RESOLVED")
                .orElseThrow(() -> new TailStatusNotFoundException("The requested tail status 'RESOLVED' does not exist."));
        tail.setStatus(resolvedStatus);
        // set tail resolved timestamp
        tail.setResolvedTimestamp(Instant.now());
        // set tail assigned user id to the resolverUser's ID from the request
        tail.setAssignedUserId(resolverUser.getId());

        if (!request.getNote().isBlank()) {
            NoteEntity noteEntity = new NoteEntity();
            noteEntity.setTail(tail);
            noteEntity.setUser(resolverUser); // The note should be associated with the resolverUser
            noteEntity.setContent(request.getNote());
            noteEntity.setCreatedAt(Instant.now());
            noteEntity.setHuman(true);
            noteEntity.setN1(false);
            noteEntity.setOrganization(tail.getOrganization());
            // save note
            this.noteRepository.save(noteEntity);
        }
        // save tail
        this.tailRepository.save(tail);
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
        tailResponse.setOrganizationId(tailEntity.getOrganization().getId());
        return tailResponse;
    }

    @Override
    public Page<TailResponse> getTails(TailPageRequest request, UserPrincipal currentUser) throws TailStatusNotFoundException, TailTypeNotFoundException, TailLevelNotFoundException { // Added currentUser
        // Authorization logic implemented. Users can only see tails related to their organizations.
        // If a user is part of the n1netails organization, they can only view their own tails.
        // UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(); // Removed
        List<Long> organizationIds = currentUser.getOrganizations().stream().map(OrganizationEntity::getId).toList();
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        String searchTerm = request.getSearchTerm() == null || request.getSearchTerm().isEmpty() ? "" : request.getSearchTerm();

        List<String> statuses = this.getTailStatuses(request);
        List<String> types = this.getTailTypes(request);
        List<String> levels = this.getTailLevels(request);

        boolean isN1netails = isInN1netailsOrg(currentUser);

        Page<TailSummary> tailPage;
        if (isN1netails) {
            if (currentUser.getId() == null) {
                // Handle case where user ID is null for n1netails user, perhaps throw exception or return empty page
                log.warn("User ID is null for n1netails user: {}", currentUser.getUsername());
                return Page.empty(pageable);
            }
            tailPage = tailRepository.findAllBySearchTermAndTailFilters(
                    searchTerm,
                    statuses,
                    types,
                    levels,
                    currentUser.getId(),
                    pageable
            );
        } else {
            if (organizationIds == null || organizationIds.isEmpty()) {
                // Handle case where user does not belong to any organization (and is not n1netails)
                // This might mean returning an empty page or throwing an exception, depending on business logic.
                log.warn("User {} does not belong to any organization and is not part of n1netails.", currentUser.getUsername());
                return Page.empty(pageable);
            }
            tailPage = tailRepository.findAllBySearchTermAndTailFilters(
                    searchTerm,
                    statuses,
                    types,
                    levels,
                    organizationIds,
                    pageable
            );
        }

        return tailPage.map(this::setTailSummaryResponse);
    }

    @Override
    public List<String> getTailLevels(TailPageRequest request) throws TailLevelNotFoundException {
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
        return levels;
    }

    @Override
    public List<String> getTailTypes(TailPageRequest request) throws TailTypeNotFoundException {
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
        return types;
    }

    @Override
    public List<String> getTailStatuses(TailPageRequest request) throws TailStatusNotFoundException {
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
        return statuses;
    }

    @Override
    public List<TailResponse> getTop9NewestTails(UserPrincipal currentUser) { // Added currentUser
        // Authorization logic implemented. Users can only see tails related to their organizations.
        // If a user is part of the n1netails organization, they can only view their own tails.
        // UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(); // Removed
        List<Long> organizationIds = currentUser.getOrganizations().stream().map(OrganizationEntity::getId).toList();
        Pageable pageable = PageRequest.of(0, 9);
        Page<TailSummary> tailPage;

        boolean isN1netails = isInN1netailsOrg(currentUser);

        if (isN1netails) {
            if (currentUser.getId() == null) {
                log.warn("User ID is null for n1netails user: {}", currentUser.getUsername());
                return new ArrayList<>(); // Return empty list
            }
            tailPage = tailRepository.findTop9ByAssignedUserIdOrderByTimestampDesc(currentUser.getId(), pageable);
        } else {
            if (organizationIds == null || organizationIds.isEmpty()) {
                log.warn("User {} does not belong to any organization and is not part of n1netails.", currentUser.getUsername());
                return new ArrayList<>(); // Return empty list
            }
            tailPage = tailRepository.findTop9ByOrganizationIdInOrderByTimestampDesc(organizationIds, pageable);
        }

        List<TailSummary> tailSummaryList = tailPage.getContent();
        List<TailResponse> tailResponseList = new ArrayList<>();
        tailSummaryList.forEach(tail -> {
            TailResponse tailResponse = setTailSummaryResponse(tail);
            tailResponseList.add(tailResponse);
        });
        return tailResponseList;
    }

    private static boolean isInN1netailsOrg(UserPrincipal currentUser) {
        boolean isN1netails = currentUser.getOrganizations().stream()
                .map(OrganizationEntity::getName)
                .anyMatch(N1NETAILS_ORG::equals);
        return isN1netails;
    }

    @Override
    public TailResponse setTailSummaryResponse(TailSummary tailSummary) {
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
