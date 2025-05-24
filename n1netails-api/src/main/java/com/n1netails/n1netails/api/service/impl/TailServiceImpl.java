package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.model.core.TailLevel;
import com.n1netails.n1netails.api.model.core.TailStatus;
import com.n1netails.n1netails.api.model.core.TailType;
import com.n1netails.n1netails.api.model.entity.*;
import com.n1netails.n1netails.api.repository.*;
import com.n1netails.n1netails.api.model.request.TailRequest;
import com.n1netails.n1netails.api.model.response.TailResponse;
import com.n1netails.n1netails.api.service.TailService;
import lombok.RequiredArgsConstructor;
import com.n1netails.n1netails.api.model.UserPrincipal; // Added
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication; // Added
import org.springframework.security.core.GrantedAuthority; // Added
import org.springframework.security.core.context.SecurityContextHolder; // Added
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
//    private final TailVariableRepository variableRepository;

    @Override
    public Page<TailResponse> getTails(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        Page<TailEntity> tailEntities;

        // Check for admin authority (e.g., 'user:create' which is used for admin access)
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals("user:create") || auth.equals("ROLE_ADMIN") || auth.equals("ROLE_SUPER_ADMIN")); // Added more role checks for robustness

        if (isAdmin) {
            log.info("User {} is admin. Fetching all tails.", currentUserEmail);
            tailEntities = tailRepository.findAll(pageable);
        } else {
            log.info("User {} is not admin. Fetching assigned tails.", currentUserEmail);
            UsersEntity currentUser = usersRepository.findUserByEmail(currentUserEmail).orElse(null);
            if (currentUser != null && currentUser.getUserId() != null) {
                tailEntities = tailRepository.findByAssignedUserId(currentUser.getUserId(), pageable);
            } else {
                log.warn("Could not find user or userId for email: {}. Returning empty page.", currentUserEmail);
                tailEntities = Page.empty(pageable);
            }
        }
        return tailEntities.map(this::setTailResponse);
    }

    @Override
    public TailResponse getTailById(Long id) {
        Optional<TailEntity> tail = tailRepository.findById(id);
        TailResponse tailResponse = new TailResponse();
        if (tail.isPresent()) {
            tailResponse = setTailResponse(tail.get());
        }
        return tailResponse;
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

        List<TailVariableEntity> tailVariableEntityList = new ArrayList<>();
        Map<String, String> metadata = request.getMetadata();
        metadata.forEach((key, value) -> {
            TailVariableEntity tailVariableEntity = new TailVariableEntity();
            tailVariableEntity.setKey(key);
            tailVariableEntity.setValue(value);
            // todo see if this works..
            tailVariableEntity.setTail(tailEntity);
            tailVariableEntityList.add(tailVariableEntity);
        });
        tailEntity.setCustomVariables(tailVariableEntityList);
        return tailEntity;
    }

    private TailResponse setTailResponse(TailEntity tailEntity) {
        TailResponse tailResponse  = new TailResponse();
        tailResponse.setId(tailEntity.getId());
        tailResponse.setTitle(tailEntity.getTitle());
        tailResponse.setDescription(tailEntity.getDescription());
        tailResponse.setTimestamp(tailEntity.getTimestamp());
        tailResponse.setResolvedTimestamp(tailEntity.getResolvedTimestamp());
        tailResponse.setAssignedUserId(tailEntity.getAssignedUserId());
        // TODO: Re-evaluate how to efficiently get username, for now, it might make an N+1 query if not careful
        // For now, let's assume assignedUsername can be null or fetched if the user entity is readily available
        // without an extra query per tail. The current implementation usersRepository.findUserByUserId will do N+1.
        UsersEntity user = usersRepository.findUserByUserId(tailEntity.getAssignedUserId());
        if (user != null) {
            tailResponse.setAssignedUsername(user.getUsername());
        } else {
            tailResponse.setAssignedUsername(null); // Or some default string
        }
        tailResponse.setDetails(tailEntity.getDetails());
        tailResponse.setLevel(tailEntity.getLevel().getName());
        tailResponse.setType(tailEntity.getType().getName());
        tailResponse.setStatus(tailEntity.getStatus().getName());
        Map<String, String> metadata = new HashMap<>();
        // Null check for customVariables
        if (tailEntity.getCustomVariables() != null) {
            List<TailVariableEntity> variables = tailEntity.getCustomVariables();
            variables.forEach(variable -> {
                metadata.put(variable.getKey(), variable.getValue());
            });
        }
        tailResponse.setMetadata(metadata);
        return tailResponse;
    }
}
