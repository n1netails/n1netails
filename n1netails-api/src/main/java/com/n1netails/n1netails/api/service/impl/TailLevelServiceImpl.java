package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.model.dto.TailLevel;
import com.n1netails.n1netails.api.model.entity.TailLevelEntity;
import com.n1netails.n1netails.api.model.response.TailLevelResponse;
import com.n1netails.n1netails.api.repository.TailLevelRepository;
import com.n1netails.n1netails.api.service.TailLevelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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
    public List<TailLevelResponse> getTailLevels() {
        List<TailLevelEntity> tailLevelEntities = this.tailLevelRepository.findAll();
        List<TailLevelResponse> tailLevelResponseList = new ArrayList<>();
        tailLevelEntities.forEach(entity -> {
            TailLevelResponse tailLevelResponse = generateTailLevelResponse(entity);
            tailLevelResponseList.add(tailLevelResponse);
        });
        return tailLevelResponseList;
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
    public void deleteTailLevel(Long id) {
        this.tailLevelRepository.deleteById(id);
    }

    private static TailLevelResponse generateTailLevelResponse(TailLevelEntity entity) {
        TailLevelResponse tailLevelResponse = new TailLevelResponse(entity.getId());
        tailLevelResponse.setName(entity.getName());
        tailLevelResponse.setDescription(entity.getDescription());
        return tailLevelResponse;
    }
}
