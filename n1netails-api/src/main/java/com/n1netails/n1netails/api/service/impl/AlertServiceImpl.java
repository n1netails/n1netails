package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.exception.type.N1neTokenGenerateException;
import com.n1netails.n1netails.api.exception.type.NotificationException;
import com.n1netails.n1netails.api.exception.type.OrganizationNotFoundException;
import com.n1netails.n1netails.api.model.entity.N1neTokenEntity;
import com.n1netails.n1netails.api.model.entity.OrganizationEntity;
import com.n1netails.n1netails.api.model.entity.TailEntity;
import com.n1netails.n1netails.api.model.entity.TailLevelEntity;
import com.n1netails.n1netails.api.model.entity.TailStatusEntity;
import com.n1netails.n1netails.api.model.entity.TailTypeEntity;
import com.n1netails.n1netails.api.model.entity.TailVariableEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.KudaTailRequest;
import com.n1netails.n1netails.api.repository.N1neTokenRepository;
import com.n1netails.n1netails.api.repository.OrganizationRepository;
import com.n1netails.n1netails.api.repository.TailLevelRepository;
import com.n1netails.n1netails.api.repository.TailRepository;
import com.n1netails.n1netails.api.repository.TailStatusRepository;
import com.n1netails.n1netails.api.repository.TailTypeRepository;
import com.n1netails.n1netails.api.service.AlertService;
import com.n1netails.n1netails.api.service.NotificationService;
import com.n1netails.n1netails.api.util.N1TokenGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private final OrganizationRepository organizationRepository;
    private final NotificationService notificationService;

    @Override
    public void createTail(String token, KudaTailRequest request) throws N1neTokenGenerateException {
        log.info("create tail");
        byte[] tokenHash = N1TokenGenerator.sha256(token);
        Optional<N1neTokenEntity> optionalN1neTokenEntity = this.n1neTokenRepository.findByN1TokenHash(tokenHash);
        N1neTokenEntity n1neTokenEntity = new N1neTokenEntity();
        if (optionalN1neTokenEntity.isPresent()) n1neTokenEntity = optionalN1neTokenEntity.get();
        UsersEntity usersEntity = n1neTokenEntity.getUser();
        saveTailAlert(n1neTokenEntity.getOrganization(), usersEntity, request);

        try {
            this.notificationService.sendNotificationAlert(usersEntity, request, n1neTokenEntity.getId());
        } catch (Exception e) {
            throw new NotificationException("Error occurred while sending notifications", e);
        }
    }

    @Override
    public void createManualTail(Long organizationId, UsersEntity usersEntity, KudaTailRequest request) throws OrganizationNotFoundException {
        OrganizationEntity organizationEntity = this.organizationRepository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException("Requested organization for creating manual tail not found."));
        saveTailAlert(organizationEntity, usersEntity, request);
    }

    @Override
    public void createManualTail(Long organizationId, UsersEntity usersEntity, KudaTailRequest request, Long n1neTokenId) throws OrganizationNotFoundException {
        this.createManualTail(organizationId, usersEntity, request);
        try {
            this.notificationService.sendNotificationAlert(usersEntity, request, n1neTokenId);
        } catch (Exception e) {
            throw new NotificationException("Error occurred while sending notifications", e);
        }
    }

    private void saveTailAlert(OrganizationEntity organizationEntity,
                               UsersEntity usersEntity, KudaTailRequest request) {
        TailEntity tailEntity = buildTailEntity(organizationEntity, usersEntity, request);
        tailRepository.save(tailEntity);
    }

    private TailEntity buildTailEntity(OrganizationEntity organizationEntity,
                                       UsersEntity usersEntity, KudaTailRequest request) {
        TailEntity tailEntity = new TailEntity();
        tailEntity.setAssignedUserId(usersEntity.getId());
        tailEntity.setTitle(request.getTitle());
        tailEntity.setDescription(request.getDescription());
        if (request.getTimestamp() == null) request.setTimestamp(Instant.now());
        tailEntity.setTimestamp(request.getTimestamp());
        tailEntity.setDetails(request.getDetails());
        tailEntity.setOrganization(organizationEntity);
        tailEntity.setTitle(request.getTitle());
        tailEntity.setDescription(request.getDescription());
        tailEntity.setTimestamp(request.getTimestamp());
        tailEntity.setDetails(request.getDetails());
        tailEntity.setOrganization(organizationEntity);

        this.attachTailLevel(tailEntity, request);
        this.attachTailType(tailEntity, request);
        this.attachTailStatus(tailEntity);
        this.attachTailMetadata(tailEntity, request);
        return tailEntity;
    }

    private void attachTailLevel(TailEntity tailEntity, KudaTailRequest request) {
        String levelName = (request.getLevel() == null || request.getLevel().isBlank()) ? INFO : request.getLevel();
        boolean deletable = !(request.getLevel() == null || request.getLevel().isBlank());

        TailLevelEntity tailLevelEntity = levelRepository.findTailLevelByName(levelName)
                .orElseGet(() -> {
                    try {
                        TailLevelEntity newLevel = new TailLevelEntity();
                        newLevel.setName(levelName);
                        newLevel.setDeletable(deletable);
                        return levelRepository.saveAndFlush(newLevel);
                    } catch (org.springframework.dao.DataIntegrityViolationException e) {
                        return levelRepository.findTailLevelByName(levelName)
                                .orElseThrow(() -> e);
                    }
                });
        tailEntity.setLevel(tailLevelEntity);
    }

    private void attachTailType(TailEntity tailEntity, KudaTailRequest request) {
        String typeName = (request.getType() == null || request.getType().isBlank()) ? SYSTEM_ALERT : request.getType();
        boolean deletable = !(request.getType() == null || request.getType().isBlank());

        TailTypeEntity tailTypeEntity = typeRepository.findTailTypeByName(typeName)
                .orElseGet(() -> {
                    try {
                        TailTypeEntity newType = new TailTypeEntity();
                        newType.setName(typeName);
                        newType.setDeletable(deletable);
                        return typeRepository.saveAndFlush(newType);
                    } catch (org.springframework.dao.DataIntegrityViolationException e) {
                        return typeRepository.findTailTypeByName(typeName)
                                .orElseThrow(() -> e);
                    }
                });
        tailEntity.setType(tailTypeEntity);
    }

    private void attachTailStatus(TailEntity tailEntity) {
        TailStatusEntity tailStatusEntity = statusRepository.findTailStatusByName(NEW)
                .orElseGet(() -> {
                    try {
                        TailStatusEntity newStatus = new TailStatusEntity();
                        newStatus.setName(NEW);
                        newStatus.setDeletable(false);
                        return statusRepository.saveAndFlush(newStatus);
                    } catch (org.springframework.dao.DataIntegrityViolationException e) {
                        return statusRepository.findTailStatusByName(NEW)
                                .orElseThrow(() -> e);
                    }
                });
        tailEntity.setStatus(tailStatusEntity);
    }

    private void attachTailMetadata(TailEntity tailEntity, KudaTailRequest request) {
        if (request.getMetadata() != null) {
            log.info("mapping tail variables");
            List<TailVariableEntity> tailVariableEntities = new ArrayList<>();
            request.getMetadata().forEach((k, v) -> {
                TailVariableEntity tailVariable = new TailVariableEntity();
                tailVariable.setKey(k);
                tailVariable.setValue(v);
                tailVariable.setTail(tailEntity);
                tailVariableEntities.add(tailVariable);
            });
            tailEntity.setCustomVariables(tailVariableEntities);
        }
    }
}
