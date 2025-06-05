package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.exception.type.TailLevelNotFoundException;
import com.n1netails.n1netails.api.exception.type.TailNotFoundException;
import com.n1netails.n1netails.api.exception.type.TailStatusNotFoundException;
import com.n1netails.n1netails.api.exception.type.TailTypeNotFoundException;
import com.n1netails.n1netails.api.model.core.TailLevel;
import com.n1netails.n1netails.api.model.core.TailStatus;
import com.n1netails.n1netails.api.model.core.TailType;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.dto.TailSummary;
import com.n1netails.n1netails.api.model.entity.*;
import com.n1netails.n1netails.api.model.request.ResolveTailRequest;
import com.n1netails.n1netails.api.model.request.TailPageRequest;
import com.n1netails.n1netails.api.model.response.TailResponse;
import com.n1netails.n1netails.api.repository.*;
import com.n1netails.n1netails.api.service.TailService;
import com.n1netails.n1netails.api.util.TailSpecificationBuilder;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("tailService")
public class TailServiceImpl implements TailService {

    private static final String N1NETAILS_ORGANIZATION_NAME = "n1netails";
    public static final String REQUESTED_TAIL_NOT_FOUND = "Requested Tail Not Found.";

    private final TailRepository tailRepository;
    private final UserRepository usersRepository;
    private final TailLevelRepository levelRepository;
    private final TailTypeRepository typeRepository;
    private final TailStatusRepository statusRepository;
//    private final TailVariableRepository variableRepository;
    private final NoteRepository noteRepository;

    // todo consider removing this or adding pagination
//    @Override
//    public List<TailResponse> getTails() {
//        List<TailEntity> tailEntities = tailRepository.findAll();
//        List<TailResponse> tailResponseList = new ArrayList<>();
//        tailEntities.forEach(tail -> {
//            TailResponse tailResponse = setTailResponse(tail);
//            tailResponseList.add(tailResponse);
//        });
//        return tailResponseList;
//    }

    @Override
    public TailResponse getTailById(Long id) {
        // TODO UPDATE THIS SO ONLY THE USER AND USERS IN THE SAME THE ORGANIZATION CAN GET THE TAIL
        // Authorization for getTailById is handled in TailController
        Optional<TailEntity> tailOptional = tailRepository.findById(id);
        if (tailOptional.isEmpty()) {
            log.warn("Tail with ID {} not found.", id);
            throw new TailNotFoundException(REQUESTED_TAIL_NOT_FOUND + " ID: " + id);
        }
        TailEntity tail = tailOptional.get();
        TailResponse tailResponse = setTailResponse(tail);
        // Populate organizationId and userId for the controller's authorization check
        if (tail.getOrganization() != null) {
            tailResponse.setOrganizationId(tail.getOrganization().getId());
        }
        if (tail.getUser() != null) { // Assuming TailEntity has a UserEntity field named 'user' for the owner
            tailResponse.setUserId(tail.getUser().getId());
        }
        return tailResponse;
    }

//    @Override
//    public TailResponse createTail(TailRequest request) {
//        TailEntity tailEntity = setTail(request);
//        tailEntity = tailRepository.save(tailEntity);
//        return setTailResponse(tailEntity);
//    }

//    @Override
//    public TailResponse updateTail(Long id, TailRequest request) {
//        TailEntity updatedTailEntity = new TailEntity();
//        Optional<TailEntity> tail = tailRepository.findById(id);
//        if (tail.isPresent()) {
//            updatedTailEntity = setTail(request, tail.get());
//            updatedTailEntity = tailRepository.save(updatedTailEntity);
//        } else {
//            log.error("updateTail - " + REQUESTED_TAIL_NOT_FOUND);
//        }
//        return setTailResponse(updatedTailEntity);
//    }

//    @Override
//    public void deleteTail(Long id) {
//        tailRepository.deleteById(id);
//    }

    @Override
    public TailResponse updateStatus(Long id, TailStatus tailStatus) {
        // TODO UPDATE THIS SO ONLY THE USER AND USERS IN THE SAME THE ORGANIZATION CAN CHANGE THE TAIL STATUS
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

//    @Override
//    public TailResponse updateTailLevel(Long id, TailLevel tailLevel) {
//        // TODO UPDATE THIS SO ONLY THE USER AND USERS IN THE SAME THE ORGANIZATION CAN UPDATE THE TAIL LEVEL
//        if (tailLevel.getDescription().isBlank()) tailLevel.setDescription(null);
//
//        Optional<TailEntity> tail = tailRepository.findById(id);
//        TailEntity updatedTailEntity = new TailEntity();
//        if (tail.isPresent()) {
//            updatedTailEntity = tail.get();
//            Optional<TailLevelEntity> newLevel = levelRepository.findTailLevelByName(tailLevel.getName());
//            if (newLevel.isPresent()) {
//                TailLevelEntity level = newLevel.get();
//                level.setDescription(tailLevel.getDescription() != null ? tailLevel.getDescription() : level.getDescription());
//                updatedTailEntity.setLevel(level);
//            } else {
//                TailLevelEntity createdLevel = new TailLevelEntity();
//                createdLevel.setName(tailLevel.getName());
//                createdLevel.setDescription(tailLevel.getDescription());
//                createdLevel = levelRepository.save(createdLevel);
//                updatedTailEntity.setLevel(createdLevel);
//            }
//            tailRepository.save(updatedTailEntity);
//        } else {
//            log.error("updateTailLevel - " + REQUESTED_TAIL_NOT_FOUND);
//        }
//        return setTailResponse(updatedTailEntity);
//    }
//
//    @Override
//    public TailResponse updateTailType(Long id, TailType tailType) {
//        // TODO UPDATE THIS SO ONLY THE USER AND USERS IN THE SAME THE ORGANIZATION CAN UPDATE THE TAIL TYPE
//        if (tailType.getDescription().isBlank()) tailType.setDescription(null);
//
//        Optional<TailEntity> tail = tailRepository.findById(id);
//        TailEntity updatedTailEntity = new TailEntity();
//        if (tail.isPresent()) {
//            updatedTailEntity = tail.get();
//            Optional<TailTypeEntity> newType = typeRepository.findTailTypeByName(tailType.getName());
//            if (newType.isPresent()) {
//                TailTypeEntity type = newType.get();
//                type.setDescription(tailType.getDescription() != null ? tailType.getDescription() : type.getDescription());
//                updatedTailEntity.setType(type);
//            } else {
//                TailTypeEntity createdType = new TailTypeEntity();
//                createdType.setName(tailType.getName());
//                createdType.setDescription(tailType.getDescription());
//                createdType = typeRepository.save(createdType);
//                updatedTailEntity.setType(createdType);
//            }
//            tailRepository.save(updatedTailEntity);
//        } else {
//            log.error("updateTailType - " + REQUESTED_TAIL_NOT_FOUND);
//        }
//        return setTailResponse(updatedTailEntity);
//    }

    @Override
    public void markResolved(ResolveTailRequest request) throws TailNotFoundException, TailStatusNotFoundException {
        // TODO UPDATE THIS SO ONLY THE USER AND USERS IN THE SAME THE ORGANIZATION CAN MARK THE TAIL AS RESOLVED
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

//    private TailEntity setTail(TailRequest request) {
//        TailEntity tailEntity = new TailEntity();
//        return setTail(request, tailEntity);
//    }

//    private TailEntity setTail(TailRequest request, TailEntity tailEntity) {
//        tailEntity.setTitle(request.getTitle());
//        tailEntity.setDescription(request.getDescription());
//        tailEntity.setTimestamp(request.getTimestamp());
//        tailEntity.setAssignedUserId(request.getAssignedUserId());
//        tailEntity.setDetails(request.getDetails());
//
//        TailLevelEntity tailLevelEntity = new TailLevelEntity();
//        tailLevelEntity.setName(request.getLevel().getName());
//        tailLevelEntity.setDescription(request.getLevel().getDescription());
//        tailEntity.setLevel(tailLevelEntity);
//
//        TailTypeEntity tailTypeEntity = new TailTypeEntity();
//        tailTypeEntity.setName(request.getType().getName());
//        tailTypeEntity.setDescription(request.getType().getDescription());
//        tailEntity.setType(tailTypeEntity);
//
//        TailStatusEntity tailStatusEntity = new TailStatusEntity();
//        tailStatusEntity.setName(request.getStatus());
//        tailEntity.setStatus(tailStatusEntity);
//
//        List<TailVariableEntity> tailVariableEntityList = getTailVariableEntities(request, tailEntity);
//        tailEntity.setCustomVariables(tailVariableEntityList);
//        return tailEntity;
//    }

//    private static List<TailVariableEntity> getTailVariableEntities(TailRequest request, TailEntity tailEntity) {
//        List<TailVariableEntity> tailVariableEntityList = new ArrayList<>();
//        Map<String, String> metadata = request.getMetadata();
//        metadata.forEach((key, value) -> {
//            TailVariableEntity tailVariableEntity = new TailVariableEntity();
//            tailVariableEntity.setKey(key);
//            tailVariableEntity.setValue(value);
//            tailVariableEntity.setTail(tailEntity);
//            tailVariableEntityList.add(tailVariableEntity);
//        });
//        return tailVariableEntityList;
//    }

    private TailResponse setTailResponse(TailEntity tailEntity) {
        TailResponse tailResponse = new TailResponse();
        tailResponse.setId(tailEntity.getId());
        tailResponse.setTitle(tailEntity.getTitle());
        tailResponse.setDescription(tailEntity.getDescription());
        tailResponse.setTimestamp(tailEntity.getTimestamp());
        tailResponse.setResolvedTimestamp(tailEntity.getResolvedTimestamp());
        tailResponse.setAssignedUserId(tailEntity.getAssignedUserId());
        if (tailEntity.getAssignedUserId() != null) {
            UsersEntity assignedUser = usersRepository.findUserById(tailEntity.getAssignedUserId());
            if (assignedUser != null) {
                tailResponse.setAssignedUsername(assignedUser.getUsername());
            }
        }
        tailResponse.setDetails(tailEntity.getDetails());
        if (tailEntity.getLevel() != null) tailResponse.setLevel(tailEntity.getLevel().getName());
        if (tailEntity.getType() != null) tailResponse.setType(tailEntity.getType().getName());
        if (tailEntity.getStatus() != null) tailResponse.setStatus(tailEntity.getStatus().getName());

        Map<String, String> metadata = new HashMap<>();
        List<TailVariableEntity> variables = tailEntity.getCustomVariables();
        if (variables != null) {
            variables.forEach(variable -> metadata.put(variable.getKey(), variable.getValue()));
        }
        tailResponse.setMetadata(metadata);

        // Populate owner user ID and organization ID for authorization checks
        if (tailEntity.getUser() != null) {
            tailResponse.setUserId(tailEntity.getUser().getId());
        }
        if (tailEntity.getOrganization() != null) {
            tailResponse.setOrganizationId(tailEntity.getOrganization().getId());
        }
        return tailResponse;
    }

    @Override
    public Page<TailResponse> getTails(TailPageRequest request, UserPrincipal currentUser) throws TailStatusNotFoundException, TailTypeNotFoundException, TailLevelNotFoundException {
        Set<Long> userOrgIds = currentUser.getOrganizations().stream()
                .map(OrganizationEntity::getId)
                .collect(Collectors.toSet());

        if (userOrgIds.isEmpty()) {
            log.info("User {} has no organization memberships, returning empty page.", currentUser.getUsername());
            return Page.empty();
        }

        boolean isN1neTailsOrgMember = currentUser.getOrganizations().stream()
                .anyMatch(org -> N1NETAILS_ORGANIZATION_NAME.equals(org.getName()));

        Specification<TailEntity> authSpec = (root, query, cb) -> {
            Predicate orgPredicate = root.get("organization").get("id").in(userOrgIds);
            if (isN1neTailsOrgMember) {
                // Assuming TailEntity has a 'user' field representing the owner (UsersEntity)
                Predicate userPredicate = cb.equal(root.get("user").get("id"), currentUser.getId());
                return cb.and(orgPredicate, userPredicate);
            } else {
                return orgPredicate;
            }
        };

        Specification<TailEntity> baseSpec = TailSpecificationBuilder.build(request, statusRepository, typeRepository, levelRepository);
        Specification<TailEntity> finalSpec = Specification.where(authSpec).and(baseSpec);

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by(Sort.Direction.DESC, "timestamp")); // Default sort
        // Consider adding sort options from TailPageRequest if available

        Page<TailEntity> tailPage = tailRepository.findAll(finalSpec, pageable);
        return tailPage.map(this::setTailResponse); // Reuse setTailResponse which now populates all needed fields
    }

    @Override
    public List<TailResponse> getTop9NewestTails(UserPrincipal currentUser) {
        Set<Long> userOrgIds = currentUser.getOrganizations().stream()
                .map(OrganizationEntity::getId)
                .collect(Collectors.toSet());

        if (userOrgIds.isEmpty()) {
            log.info("User {} has no organization memberships, returning empty list for top 9.", currentUser.getUsername());
            return Collections.emptyList();
        }

        boolean isN1neTailsOrgMember = currentUser.getOrganizations().stream()
                .anyMatch(org -> N1NETAILS_ORGANIZATION_NAME.equals(org.getName()));

        Pageable limit = PageRequest.of(0, 9, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<TailEntity> tailEntities;

        if (isN1neTailsOrgMember) {
            tailEntities = tailRepository.findByOrganization_IdInAndUser_IdOrderByCreatedAtDesc(userOrgIds, currentUser.getId(), limit);
        } else {
            tailEntities = tailRepository.findByOrganization_IdInOrderByCreatedAtDesc(userOrgIds, limit);
        }

        return tailEntities.stream().map(this::setTailResponse).collect(Collectors.toList());
    }

    // This method converts TailSummary to TailResponse.
    // We will rely on setTailResponse(TailEntity tailEntity) for consistency now.
    // If TailSummary is still strictly needed for some paths, it might need its own population logic for orgId/userId.
    private TailResponse setTailSummaryResponse(TailSummary tailSummary) {
        TailResponse tailResponse = new TailResponse();
        tailResponse.setId(tailSummary.getId());
        tailResponse.setTitle(tailSummary.getTitle());
        tailResponse.setDescription(tailSummary.getDescription());
        tailResponse.setTimestamp(tailSummary.getTimestamp());
        tailResponse.setResolvedTimestamp(tailSummary.getResolvedTimestamp());
        tailResponse.setAssignedUserId(tailSummary.getAssignedUserId());

        // For TailSummary, we might not have the full UserEntity or OrganizationEntity.
        // The controller methods requiring these (like getById) will use getTailById, which fetches the full TailEntity.
        // For lists (getTails, getTop9NewestTails), the primary filtering is done in the query.
        // If TailResponse *always* needs userId (owner) and organizationId, TailSummary might need to include them.
        // For now, assuming this is acceptable for list views where primary auth is by query.

        if (tailSummary.getAssignedUserId() != null) {
            UsersEntity user = usersRepository.findUserById(tailSummary.getAssignedUserId());
            if (user != null) {
                tailResponse.setAssignedUsername(user.getUsername());
            }
        }
        tailResponse.setLevel(tailSummary.getLevel());
        tailResponse.setType(tailSummary.getType());
        tailResponse.setStatus(tailSummary.getStatus());
        return tailResponse;
    }
}
