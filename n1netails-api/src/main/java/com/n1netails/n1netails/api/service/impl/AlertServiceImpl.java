package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.model.entity.*;
import com.n1netails.n1netails.api.model.request.KudaTailRequest;
import com.n1netails.n1netails.api.repository.*;
import com.n1netails.n1netails.api.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Instant;
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

        TailEntity tailEntity = new TailEntity();
        tailEntity.setAssignedUserId(usersEntity.getId());
        tailEntity.setTitle(request.getTitle());
        tailEntity.setDescription(request.getDescription());
        if (request.getTimestamp() == null) request.setTimestamp(Instant.now());
        tailEntity.setTimestamp(request.getTimestamp());
        tailEntity.setDetails(request.getDetails());
        tailEntity.setOrganization(n1neTokenEntity.getOrganization());

        log.info("finding extra tail info");
        // tail level
        log.info("tail level");
        TailLevelEntity tailLevelEntity;
        if (this.levelRepository.findTailLevelByName(request.getLevel()).isPresent()) {
            tailLevelEntity = this.levelRepository.findTailLevelByName(request.getLevel()).get();
        } else if (request.getLevel() == null || request.getLevel().isBlank()) {
            tailLevelEntity = this.levelRepository.findTailLevelByName(INFO)
                    .orElseGet(() -> {
                        TailLevelEntity newLevel = new TailLevelEntity();
                        newLevel.setName(INFO);
                        newLevel.setDeletable(false);
                        return this.levelRepository.save(newLevel);
                    });
        } else {
            tailLevelEntity = new TailLevelEntity();
            tailLevelEntity.setName(request.getLevel());
            tailLevelEntity.setDeletable(true);
            tailLevelEntity = this.levelRepository.save(tailLevelEntity);
        }

        // tail type
        log.info("tail type");
        TailTypeEntity tailTypeEntity;
        if (this.typeRepository.findTailTypeByName(request.getType()).isPresent()) {
            tailTypeEntity = this.typeRepository.findTailTypeByName(request.getType()).get();
        } else if (request.getType() == null || request.getType().isBlank()) {
            tailTypeEntity = this.typeRepository.findTailTypeByName(SYSTEM_ALERT)
                    .orElseGet(() -> {
                        TailTypeEntity newType = new TailTypeEntity();
                        newType.setName(SYSTEM_ALERT);
                        newType.setDeletable(false);
                        return this.typeRepository.save(newType);
                    });
        } else {
            tailTypeEntity = new TailTypeEntity();
            tailTypeEntity.setName(request.getType());
            tailTypeEntity.setDeletable(true);
            tailTypeEntity = this.typeRepository.save(tailTypeEntity);
        }

        // tail status (set NEW status for incoming tails)
        log.info("tail status");
        TailStatusEntity tailStatusEntity;
        if (this.statusRepository.findTailStatusByName(NEW).isPresent()) {
            tailStatusEntity = this.statusRepository.findTailStatusByName(NEW).get();
        } else {
            tailStatusEntity = new TailStatusEntity();
            tailStatusEntity.setName(NEW);
            tailStatusEntity.setDeletable(false);
            tailStatusEntity = this.statusRepository.save(tailStatusEntity);
        }

        tailEntity.setLevel(tailLevelEntity);
        tailEntity.setType(tailTypeEntity);
        tailEntity.setStatus(tailStatusEntity);


        log.info("save tail");
        tailEntity = this.tailRepository.save(tailEntity);

        if (request.getMetadata() != null) {
            log.info("mapping tail variables");
            log.info(request.getMetadata().toString());
            List<TailVariableEntity> tailVariableEntities = new ArrayList<>();
            TailEntity finalTailEntity = tailEntity;
            request.getMetadata().forEach((k, v) -> {
                TailVariableEntity tailVariable = new TailVariableEntity();
                tailVariable.setKey(k);
                tailVariable.setValue(v);
                tailVariable.setTail(finalTailEntity);
                tailVariableEntities.add(tailVariable);
            });

            finalTailEntity.setCustomVariables(tailVariableEntities);
            this.tailRepository.save(finalTailEntity);
        }
    }
}
