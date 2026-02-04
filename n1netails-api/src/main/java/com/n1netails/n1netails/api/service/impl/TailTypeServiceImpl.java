package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.exception.type.TailTypeNotFoundException;
import com.n1netails.n1netails.api.model.core.TailType;
import com.n1netails.n1netails.api.model.entity.TailTypeEntity;
import com.n1netails.n1netails.api.model.request.PageRequest;
import com.n1netails.n1netails.api.model.response.TailTypeResponse;
import com.n1netails.n1netails.api.repository.TailTypeRepository;
import com.n1netails.n1netails.api.service.TailTypeService;
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
@Qualifier("tailTypeService")
public class TailTypeServiceImpl implements TailTypeService {

    public static final String TAIL_TYPE_DOES_NOT_EXIST = "Tail Type does not exist: ";

    private final TailTypeRepository tailTypeRepository;

    @Override
    public Page<TailTypeResponse> getTailTypes(PageRequest request) {
        Sort sort = Sort.by(request.getSortDirection(), request.getSortBy());
        Pageable pageable = org.springframework.data.domain.PageRequest.of(request.getPageNumber(), request.getPageSize(), sort);
        Page<TailTypeEntity> tailTypeEntities;
        if (request.getSearchTerm() != null && !request.getSearchTerm().isEmpty()) {
            tailTypeEntities = tailTypeRepository.findByNameContainingIgnoreCase(request.getSearchTerm(), pageable);
        } else {
            tailTypeEntities = tailTypeRepository.findAll(pageable);
        }
        List<TailTypeResponse> tailTypeResponseList = new ArrayList<>();
        tailTypeEntities.forEach(entity -> {
            TailTypeResponse tailTypeResponse = generateTailTypeResponse(entity);
            tailTypeResponseList.add(tailTypeResponse);
        });
        return new PageImpl<>(tailTypeResponseList, pageable, tailTypeEntities.getTotalElements());
    }

    @Override
    public TailTypeResponse getTailTypeById(Long id) {
        TailTypeEntity tailTypeEntity = this.tailTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(TAIL_TYPE_DOES_NOT_EXIST + id));
        return generateTailTypeResponse(tailTypeEntity);
    }

    @Override
    public TailTypeResponse createTailType(TailType request) {
        this.tailTypeRepository.findTailTypeByName(request.getName()).ifPresent(s -> {
            throw new IllegalArgumentException("Tail Type already exists with name: " + request.getName());
        });
        TailTypeEntity tailTypeEntity = new TailTypeEntity();
        tailTypeEntity.setName(request.getName());
        tailTypeEntity.setDescription(request.getDescription());
        tailTypeEntity.setDeletable(request.isDeletable());
        tailTypeEntity = this.tailTypeRepository.save(tailTypeEntity);
        return generateTailTypeResponse(tailTypeEntity);
    }

    @Override
    public TailTypeResponse updateTailType(Long id, TailType request) {
        TailTypeEntity tailTypeEntity = this.tailTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(TAIL_TYPE_DOES_NOT_EXIST + id));
        this.tailTypeRepository.findTailTypeByName(request.getName()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new IllegalArgumentException("Tail Type already exists with name: " + request.getName());
            }
        });
        tailTypeEntity.setName(request.getName());
        tailTypeEntity.setDescription(request.getDescription());
        tailTypeEntity = this.tailTypeRepository.save(tailTypeEntity);
        return generateTailTypeResponse(tailTypeEntity);
    }

    @Override
    public void deleteTailType(Long id) throws TailTypeNotFoundException {
        TailTypeEntity tailTypeEntity = this.tailTypeRepository.findById(id)
                .orElseThrow(() -> new TailTypeNotFoundException("Tail Type Does Not Exist."));
        if (tailTypeEntity.isDeletable()) this.tailTypeRepository.deleteById(id);
    }

    private TailTypeResponse generateTailTypeResponse(TailTypeEntity entity) {
        TailTypeResponse tailTypeResponse = new TailTypeResponse(entity.getId());
        tailTypeResponse.setName(entity.getName());
        tailTypeResponse.setDescription(entity.getDescription());
        tailTypeResponse.setDeletable(entity.isDeletable());
        return tailTypeResponse;
    }
}
