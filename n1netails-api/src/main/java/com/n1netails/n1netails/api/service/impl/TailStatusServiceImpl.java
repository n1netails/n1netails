package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.model.core.TailStatus;
import com.n1netails.n1netails.api.model.entity.TailStatusEntity;
import com.n1netails.n1netails.api.model.response.TailStatusResponse;
import com.n1netails.n1netails.api.repository.TailStatusRepository;
import com.n1netails.n1netails.api.service.TailStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("tailStatusService")
public class TailStatusServiceImpl implements TailStatusService {

    public static final String TAIL_STATUS_DOES_NOT_EXIST = "Tail Status does not exist: ";

    private final TailStatusRepository tailStatusRepository;

    @Override
    public List<TailStatusResponse> getTailStatusList() {
        List<TailStatusEntity> tailStatusEntities = this.tailStatusRepository.findAll();
        List<TailStatusResponse> tailStatusResponseList = new ArrayList<>();
        tailStatusEntities.forEach(entity -> {
            TailStatusResponse tailStatusResponse = generateTailStatusResponse(entity);
            tailStatusResponseList.add(tailStatusResponse);
        });
        return tailStatusResponseList;
    }

    @Override
    public TailStatusResponse getTailStatusById(Long id) {
        TailStatusEntity tailStatusEntity = this.tailStatusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(TAIL_STATUS_DOES_NOT_EXIST + id));
        return generateTailStatusResponse(tailStatusEntity);
    }

    @Override
    public TailStatusResponse createTailStatus(TailStatus request) {
        TailStatusEntity tailStatusEntity = new TailStatusEntity();
        tailStatusEntity.setName(request.getName());
        tailStatusEntity = this.tailStatusRepository.save(tailStatusEntity);
        return generateTailStatusResponse(tailStatusEntity);
    }

    @Override
    public TailStatusResponse updateTailStatus(Long id, TailStatus request) {
        TailStatusEntity tailStatusEntity = this.tailStatusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(TAIL_STATUS_DOES_NOT_EXIST + id));
        tailStatusEntity.setName(request.getName());
        tailStatusEntity = this.tailStatusRepository.save(tailStatusEntity);
        return generateTailStatusResponse(tailStatusEntity);
    }

    @Override
    public void deleteTailStatus(Long id) {
        this.tailStatusRepository.deleteById(id);
    }

    private TailStatusResponse generateTailStatusResponse(TailStatusEntity entity) {
        TailStatusResponse tailStatusResponse = new TailStatusResponse(entity.getId());
        tailStatusResponse.setName(entity.getName());
        return tailStatusResponse;
    }
}
