package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.exception.type.TailLevelNotFoundException;
import com.n1netails.n1netails.api.model.core.TailLevel;
import com.n1netails.n1netails.api.model.entity.TailLevelEntity;
import com.n1netails.n1netails.api.model.request.PageRequest;
import com.n1netails.n1netails.api.model.response.TailLevelResponse;
import com.n1netails.n1netails.api.repository.TailLevelRepository;
import com.n1netails.n1netails.api.service.TailLevelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("tailLevelService")
public class TailLevelServiceImpl implements TailLevelService {

    public static final String TAIL_LEVEL_DOES_NOT_EXIST = "Tail Level does not exist: ";

    private final TailLevelRepository tailLevelRepository;

    @Override
    public Page<TailLevelResponse> getTailLevels(PageRequest request) {
        Sort sort = Sort.by(request.getSortDirection(), request.getSortBy());
        Pageable pageable = org.springframework.data.domain.PageRequest.of(request.getPageNumber(), request.getPageSize(), sort);
        Page<TailLevelEntity> tailLevelEntities;
        if (request.getSearchTerm() != null && !request.getSearchTerm().isEmpty()) {
            tailLevelEntities = tailLevelRepository.findByNameContainingIgnoreCase(request.getSearchTerm(), pageable);
        } else {
            tailLevelEntities = tailLevelRepository.findAll(pageable);
        }
        List<TailLevelResponse> tailLevelResponseList = new ArrayList<>();
        tailLevelEntities.forEach(entity -> {
            TailLevelResponse tailLevelResponse = generateTailLevelResponse(entity);
            tailLevelResponseList.add(tailLevelResponse);
        });
        return new PageImpl<>(tailLevelResponseList, pageable, tailLevelEntities.getTotalElements());
    }

    @Override
    public TailLevelResponse getTailLevelById(Long id) {
        TailLevelEntity tailLevelEntity = this.tailLevelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(TAIL_LEVEL_DOES_NOT_EXIST + id));
        return generateTailLevelResponse(tailLevelEntity);
    }

    @Override
    public TailLevelResponse createTailLevel(TailLevel request) {
        TailLevelEntity tailLevelEntity = new TailLevelEntity();
        tailLevelEntity.setName(request.getName());
        tailLevelEntity.setDescription(request.getDescription());
        tailLevelEntity.setDeletable(request.isDeletable());
        tailLevelEntity = this.tailLevelRepository.save(tailLevelEntity);
        return generateTailLevelResponse(tailLevelEntity);
    }

    @Override
    public TailLevelResponse updateTailLevel(Long id, TailLevel request) {
        TailLevelEntity tailLevelEntity = this.tailLevelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(TAIL_LEVEL_DOES_NOT_EXIST + id));
        tailLevelEntity.setName(request.getName());
        tailLevelEntity.setDescription(request.getDescription());
        tailLevelEntity = this.tailLevelRepository.save(tailLevelEntity);
        return generateTailLevelResponse(tailLevelEntity);
    }

    @Override
    public void deleteTailLevel(Long id) throws TailLevelNotFoundException {
        TailLevelEntity tailLevelEntity = this.tailLevelRepository.findById(id)
                .orElseThrow(() -> new TailLevelNotFoundException("Tail Level Does Not Exist."));
        if (tailLevelEntity.isDeletable()) this.tailLevelRepository.deleteById(id);
    }

    private static TailLevelResponse generateTailLevelResponse(TailLevelEntity entity) {
        TailLevelResponse tailLevelResponse = new TailLevelResponse(entity.getId());
        tailLevelResponse.setName(entity.getName());
        tailLevelResponse.setDescription(entity.getDescription());
        tailLevelResponse.setDeletable(entity.isDeletable());
        return tailLevelResponse;
    }
}
