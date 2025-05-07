package com.n1ne.n1netails.api.service.impl;

import com.n1ne.n1netails.api.model.dto.TailLevelDto;
import com.n1ne.n1netails.api.model.dto.TailStatusDto;
import com.n1ne.n1netails.api.model.dto.TailTypeDto;
import com.n1ne.n1netails.api.model.entity.*;
import com.n1ne.n1netails.api.repository.*;
import com.n1ne.n1netails.api.model.request.TailRequest;
import com.n1ne.n1netails.api.model.response.TailResponse;
import com.n1ne.n1netails.api.service.TailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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
    public List<TailResponse> getTails() {
        List<Tail> tails = tailRepository.findAll();
        List<TailResponse> tailResponseList = new ArrayList<>();
        tails.forEach(tail -> {
            TailResponse tailResponse = setTailResponse(tail);
            tailResponseList.add(tailResponse);
        });
        return tailResponseList;
    }

    @Override
    public TailResponse getTailById(Long id) {
        Optional<Tail> tail = tailRepository.findById(id);
        TailResponse tailResponse = new TailResponse();
        if (tail.isPresent()) {
            tailResponse = setTailResponse(tail.get());
        }
        return tailResponse;
    }

    @Override
    public TailResponse createTail(TailRequest request) {
        Tail tail = setTail(request);
        tail = tailRepository.save(tail);
        return setTailResponse(tail);
    }

    @Override
    public TailResponse updateTail(Long id, TailRequest request) {
        Tail updatedTail = new Tail();
        Optional<Tail> tail = tailRepository.findById(id);
        if (tail.isPresent()) {
            updatedTail = setTail(request, tail.get());
            updatedTail = tailRepository.save(updatedTail);
        } else {
            log.error("updateTail - " + REQUESTED_TAIL_NOT_FOUND);
        }
        return setTailResponse(updatedTail);
    }

    @Override
    public void deleteTail(Long id) {
        tailRepository.deleteById(id);
    }

    @Override
    public TailResponse updateTailStatus(Long id, TailStatusDto tailStatus) {
        Optional<Tail> tail = tailRepository.findById(id);
        Tail updatedTail = new Tail();
        if (tail.isPresent()) {
            updatedTail = tail.get();
            Optional<TailStatus> newStatus = statusRepository.findTailStatusByName(tailStatus.getName());
            if (newStatus.isPresent()) {
                updatedTail.setStatus(newStatus.get());
            } else {
                TailStatus createdStatus = new TailStatus();
                createdStatus.setName(tailStatus.getName());
                createdStatus = statusRepository.save(createdStatus);
                updatedTail.setStatus(createdStatus);
            }
            tailRepository.save(updatedTail);
        } else {
            log.error("updateTailStatus - " + REQUESTED_TAIL_NOT_FOUND);
        }
        return setTailResponse(updatedTail);
    }

    @Override
    public TailResponse updateTailLevel(Long id, TailLevelDto tailLevel) {
        if (tailLevel.getDescription().isBlank()) tailLevel.setDescription(null);

        Optional<Tail> tail = tailRepository.findById(id);
        Tail updatedTail = new Tail();
        if (tail.isPresent()) {
            updatedTail = tail.get();
            Optional<TailLevel> newLevel = levelRepository.findTailLevelByName(tailLevel.getName());
            if (newLevel.isPresent()) {
                TailLevel level = newLevel.get();
                level.setDescription(tailLevel.getDescription() != null ? tailLevel.getDescription() : level.getDescription());
                updatedTail.setLevel(level);
            } else {
                TailLevel createdLevel = new TailLevel();
                createdLevel.setName(tailLevel.getName());
                createdLevel.setDescription(tailLevel.getDescription());
                createdLevel = levelRepository.save(createdLevel);
                updatedTail.setLevel(createdLevel);
            }
            tailRepository.save(updatedTail);
        } else {
            log.error("updateTailLevel - " + REQUESTED_TAIL_NOT_FOUND);
        }
        return setTailResponse(updatedTail);
    }

    @Override
    public TailResponse updateTailType(Long id, TailTypeDto tailType) {
        if (tailType.getDescription().isBlank()) tailType.setDescription(null);

        Optional<Tail> tail = tailRepository.findById(id);
        Tail updatedTail = new Tail();
        if (tail.isPresent()) {
            updatedTail = tail.get();
            Optional<TailType> newType = typeRepository.findTailTypeByName(tailType.getName());
            if (newType.isPresent()) {
                TailType type = newType.get();
                type.setDescription(tailType.getDescription() != null ? tailType.getDescription() : type.getDescription());
                updatedTail.setType(type);
            } else {
                TailType createdType = new TailType();
                createdType.setName(tailType.getName());
                createdType.setDescription(tailType.getDescription());
                createdType = typeRepository.save(createdType);
                updatedTail.setType(createdType);
            }
            tailRepository.save(updatedTail);
        } else {
            log.error("updateTailType - " + REQUESTED_TAIL_NOT_FOUND);
        }
        return setTailResponse(updatedTail);
    }

    private Tail setTail(TailRequest request) {
        Tail tail = new Tail();
        return setTail(request, tail);
    }

    private Tail setTail(TailRequest request, Tail tail) {
        tail.setTitle(request.getTitle());
        tail.setDescription(request.getDescription());
        tail.setTimestamp(request.getTimestamp());
        tail.setAssignedUserId(request.getAssignedUserId());
        tail.setDetails(request.getDetails());

        TailLevel tailLevel = new TailLevel();
        tailLevel.setName(request.getTailLevel().getName());
        tailLevel.setDescription(request.getTailLevel().getDescription());
        tail.setLevel(tailLevel);

        TailType tailType = new TailType();
        tailType.setName(request.getTailType().getName());
        tailType.setDescription(request.getTailType().getDescription());
        tail.setType(tailType);

        TailStatus tailStatus = new TailStatus();
        tailStatus.setName(request.getTailStatus().getName());
        tail.setStatus(tailStatus);

        List<TailVariable> tailVariableList = new ArrayList<>();
        Map<String, String> metadata = request.getMetadata();
        metadata.forEach((key, value) -> {
            TailVariable tailVariable = new TailVariable();
            tailVariable.setKey(key);
            tailVariable.setValue(value);
            // todo see if this works..
            tailVariable.setTail(tail);
            tailVariableList.add(tailVariable);
        });
        tail.setCustomVariables(tailVariableList);
        return tail;
    }

    private TailResponse setTailResponse(Tail tail) {
        TailResponse tailResponse  = new TailResponse();
        tailResponse.setId(tail.getId());
        tailResponse.setTitle(tail.getTitle());
        tailResponse.setDescription(tail.getDescription());
        tailResponse.setTimestamp(tail.getTimestamp());
        tailResponse.setResolvedTimestamp(tail.getResolvedTimestamp());
        tailResponse.setAssignedUserId(tail.getAssignedUserId());
        Users user = usersRepository.findUserByUserId(tail.getAssignedUserId());
        tailResponse.setAssignedUsername(user.getUsername());
        tailResponse.setDetails(tail.getDetails());
        tailResponse.setLevel(tail.getLevel().getName());
        tailResponse.setType(tail.getType().getName());
        tailResponse.setStatus(tail.getStatus().getName());
        Map<String, String> metadata = new HashMap<>();
        List<TailVariable> variables = tail.getCustomVariables();
        variables.forEach(variable -> {
           metadata.put(variable.getKey(), variable.getValue());
        });
        tailResponse.setMetadata(metadata);
        return tailResponse;
    }
}
