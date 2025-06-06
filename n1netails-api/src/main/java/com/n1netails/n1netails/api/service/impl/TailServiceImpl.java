package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.exception.type.TailLevelNotFoundException;
import com.n1netails.n1netails.api.exception.type.TailNotFoundException;
import com.n1netails.n1netails.api.exception.type.TailStatusNotFoundException;
import com.n1netails.n1netails.api.exception.type.TailTypeNotFoundException;
import com.n1netails.n1netails.api.model.core.TailStatus;
import com.n1netails.n1netails.api.model.dto.TailSummary;
import com.n1netails.n1netails.api.model.entity.*;
import com.n1netails.n1netails.api.model.request.ResolveTailRequest;
import com.n1netails.n1netails.api.model.request.TailPageRequest;
import com.n1netails.n1netails.api.repository.*;
import com.n1netails.n1netails.api.model.response.TailResponse;
import com.n1netails.n1netails.api.service.TailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    private final TailRepository tailRepository;
    private final UserRepository usersRepository;
    private final TailLevelRepository levelRepository;
    private final TailTypeRepository typeRepository;
    private final TailStatusRepository statusRepository;
    private final NoteRepository noteRepository;

    @Override
    public TailResponse getTailById(Long id) {
        // TODO UPDATE THIS SO ONLY THE USER AND USERS IN THE SAME THE ORGANIZATION CAN GET THE TAIL
        Optional<TailEntity> tail = tailRepository.findById(id);
        TailResponse tailResponse = new TailResponse();
        if (tail.isPresent()) {
            tailResponse = setTailResponse(tail.get());
        }
        return tailResponse;
    }

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
    public Page<TailResponse> getTails(TailPageRequest request) throws TailStatusNotFoundException, TailTypeNotFoundException, TailLevelNotFoundException {
        // TODO MAKE SURE USERS CAN ONLY SEE TAILS RELATED TO ORGANIZATIONS THEY ARE APART OF
        // TODO IF A USER IS PART OF THE n1netails ORGANIZATION THEY CAN ONLY VIEW THEIR OWN TAILS

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

        Page<TailSummary> tailPage = tailRepository.findAllBySearchTermAndTailFilters(
                searchTerm,
                statuses,
                types,
                levels,
                pageable
        );

        return tailPage.map(this::setTailSummaryResponse);
    }

    @Override
    public List<TailResponse> getTop9NewestTails() {
        // TODO MAKE SURE USERS CAN ONLY SEE TAILS RELATED TO THEIR ORGANIZATION
        // TODO IF A USER IS PART OF THE n1netails ORGANIZATION THEY CAN ONLY VIEW THEIR OWN TAILS

        Page<TailSummary> tailPage = tailRepository.findAllByOrderByTimestampDesc(PageRequest.of(0,9));
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
