package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.model.core.TailType;
import com.n1netails.n1netails.api.model.entity.TailTypeEntity;
import com.n1netails.n1netails.api.model.response.TailTypeResponse;
import com.n1netails.n1netails.api.repository.TailTypeRepository;
import com.n1netails.n1netails.api.service.TailTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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
    public List<TailTypeResponse> getTailTypes() {
        List<TailTypeEntity> tailTypeEntities = this.tailTypeRepository.findAll();
        List<TailTypeResponse> tailTypeResponseList = new ArrayList<>();
        tailTypeEntities.forEach(entity -> {
            TailTypeResponse tailTypeResponse = generateTailTypeResponse(entity);
            tailTypeResponseList.add(tailTypeResponse);
        });
        return tailTypeResponseList;
    }

    @Override
    public TailTypeResponse getTailTypeById(Long id) {
        TailTypeEntity tailTypeEntity = this.tailTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(TAIL_TYPE_DOES_NOT_EXIST + id));
        return generateTailTypeResponse(tailTypeEntity);
    }

    @Override
    public TailTypeResponse createTailType(TailType request) {
        TailTypeEntity tailTypeEntity = new TailTypeEntity();
        tailTypeEntity.setName(request.getName());
        tailTypeEntity.setDescription(request.getDescription());
        tailTypeEntity = this.tailTypeRepository.save(tailTypeEntity);
        return generateTailTypeResponse(tailTypeEntity);
    }

    @Override
    public TailTypeResponse updateTailType(Long id, TailType request) {
        TailTypeEntity tailTypeEntity = this.tailTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(TAIL_TYPE_DOES_NOT_EXIST + id));
        tailTypeEntity.setName(request.getName());
        tailTypeEntity.setDescription(request.getDescription());
        tailTypeEntity = this.tailTypeRepository.save(tailTypeEntity);
        return generateTailTypeResponse(tailTypeEntity);
    }

    @Override
    public void deleteTailType(Long id) {
        this.tailTypeRepository.deleteById(id);
    }

    private TailTypeResponse generateTailTypeResponse(TailTypeEntity entity) {
        TailTypeResponse tailTypeResponse = new TailTypeResponse(entity.getId());
        tailTypeResponse.setName(entity.getName());
        tailTypeResponse.setDescription(entity.getDescription());
        return tailTypeResponse;
    }
}
