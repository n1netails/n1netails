package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.model.entity.N1neTokenEntity;
import com.n1netails.n1netails.api.model.entity.TailEntity;
import com.n1netails.n1netails.api.model.entity.TailLevelEntity;
import com.n1netails.n1netails.api.model.entity.TailStatusEntity;
import com.n1netails.n1netails.api.model.entity.TailTypeEntity;
import com.n1netails.n1netails.api.model.entity.TailVariableEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.KudaTailRequest;
import com.n1netails.n1netails.api.repository.N1neTokenRepository;
import com.n1netails.n1netails.api.repository.TailLevelRepository;
import com.n1netails.n1netails.api.repository.TailRepository;
import com.n1netails.n1netails.api.repository.TailStatusRepository;
import com.n1netails.n1netails.api.repository.TailTypeRepository;
import com.n1netails.n1netails.api.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("alertServiceImpl")
public class AlertServiceImpl implements AlertService {

    public static final String INFO = "INFO";
    public static final String SYSTEM_ALERT = "SYSTEM_ALERT";
    public static final String NEW = "NEW";

    private final TailRepository tailRepository;
    private final TailLevelRepository levelRepository;
    private final TailTypeRepository typeRepository;
    private final TailStatusRepository statusRepository;
    private final N1neTokenRepository n1neTokenRepository;

    @Override
    public void createTail(String token, KudaTailRequest request) {
        log.info("create tail");
        UUID uuid = UUID.fromString(token);
        Optional<N1neTokenEntity> optionalN1neTokenEntity = this.n1neTokenRepository.findByToken(uuid);
        N1neTokenEntity n1neTokenEntity = new N1neTokenEntity();
        if (optionalN1neTokenEntity.isPresent()) n1neTokenEntity = optionalN1neTokenEntity.get();
        UsersEntity usersEntity = n1neTokenEntity.getUser();

        TailEntity tailEntity = buildTailEntity(n1neTokenEntity, usersEntity, request);
        tailRepository.save(tailEntity);
    }

    private TailEntity buildTailEntity(N1neTokenEntity n1neTokenEntity, UsersEntity usersEntity,
                                       KudaTailRequest kudaTailRequest) {
        TailEntity tailEntity = new TailEntity();
        tailEntity.setAssignedUserId(usersEntity.getId());
        tailEntity.setTitle(kudaTailRequest.getTitle());
        tailEntity.setDescription(kudaTailRequest.getDescription());
        tailEntity.setTimestamp(kudaTailRequest.getTimestamp());
        tailEntity.setDetails(kudaTailRequest.getDetails());
        tailEntity.setOrganization(n1neTokenEntity.getOrganization());

        this.attachTailLevel(tailEntity, kudaTailRequest);
        this.attachTailType(tailEntity, kudaTailRequest);
        this.attachTailStatus(tailEntity, kudaTailRequest);
        this.attachTailMetadata(tailEntity, kudaTailRequest);
        return tailEntity;
    }

    private void attachTailLevel(TailEntity tailEntity, KudaTailRequest kudaTailRequest) {
        TailLevelEntity tailLevelEntity;
        Optional<TailLevelEntity> optionalTailLevel = this.levelRepository.findTailLevelByName(kudaTailRequest.getLevel());
        if (optionalTailLevel.isPresent()) {
            tailLevelEntity = optionalTailLevel.get();
        } else if (kudaTailRequest.getLevel() == null || kudaTailRequest.getLevel().isBlank()) {
            tailLevelEntity = this.levelRepository.findTailLevelByName(INFO)
                    .orElseGet(() -> {
                        TailLevelEntity newLevel = new TailLevelEntity();
                        newLevel.setName(INFO);
                        newLevel.setDeletable(false);
                        return this.levelRepository.save(newLevel);
                    });
        } else {
            tailLevelEntity = new TailLevelEntity();
            tailLevelEntity.setName(kudaTailRequest.getLevel());
            tailLevelEntity.setDeletable(true);
            tailLevelEntity = this.levelRepository.save(tailLevelEntity);
        }
        tailEntity.setLevel(tailLevelEntity);
    }

    private void attachTailType(TailEntity tailEntity, KudaTailRequest kudaTailRequest) {
        TailTypeEntity tailTypeEntity;
        Optional<TailTypeEntity> optionalTailType = this.typeRepository.findTailTypeByName(kudaTailRequest.getType());
        if (optionalTailType.isPresent()) {
            tailTypeEntity = optionalTailType.get();
        } else if (kudaTailRequest.getType() == null || kudaTailRequest.getType().isBlank()) {
            tailTypeEntity = this.typeRepository.findTailTypeByName(SYSTEM_ALERT)
                    .orElseGet(() -> {
                        TailTypeEntity newType = new TailTypeEntity();
                        newType.setName(SYSTEM_ALERT);
                        newType.setDeletable(false);
                        return this.typeRepository.save(newType);
                    });
        } else {
            tailTypeEntity = new TailTypeEntity();
            tailTypeEntity.setName(kudaTailRequest.getType());
            tailTypeEntity.setDeletable(true);
            tailTypeEntity = this.typeRepository.save(tailTypeEntity);
        }
        tailEntity.setType(tailTypeEntity);
    }

    private void attachTailStatus(TailEntity tailEntity, KudaTailRequest kudaTailRequest) {
        TailStatusEntity tailStatusEntity;
        Optional<TailStatusEntity> optionalNewTailStatus = this.statusRepository.findTailStatusByName(NEW);
        if (optionalNewTailStatus.isPresent()) {
            tailStatusEntity = optionalNewTailStatus.get();
        } else {
            tailStatusEntity = new TailStatusEntity();
            tailStatusEntity.setName(NEW);
            tailStatusEntity.setDeletable(false);
            tailStatusEntity = this.statusRepository.save(tailStatusEntity);
        }
        tailEntity.setStatus(tailStatusEntity);
    }

    private void attachTailMetadata(TailEntity tailEntity, KudaTailRequest kudaTailRequest) {
        List<TailVariableEntity> tailVariableEntities = new ArrayList<>();
        kudaTailRequest.getMetadata().forEach((k, v) -> {
            TailVariableEntity tailVariable = new TailVariableEntity();
            tailVariable.setKey(k);
            tailVariable.setValue(v);
            tailVariable.setTail(tailEntity);
            tailVariableEntities.add(tailVariable);
        });
        tailEntity.setCustomVariables(tailVariableEntities);
    }
}
