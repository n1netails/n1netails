package com.n1netails.n1netails.api.schedule;

import com.n1netails.n1netails.api.model.entity.ForgotPasswordRequestEntity;
import com.n1netails.n1netails.api.repository.ForgotPasswordRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * PurgeExpiredForgotPasswordRequestsJob
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PurgeExpiredForgotPasswordRequestsJob {

    private final ForgotPasswordRequestRepository forgotPasswordRequestRepository;

    @Scheduled(cron = "0 * * * * *")
    public void purgeExpiredForgotPasswordRequests() {
        log.info("Purge Expired Forgot Password Requests");
        List<ForgotPasswordRequestEntity> expiredRequests = forgotPasswordRequestRepository.findExpiredRequests(LocalDateTime.now());
        log.info("Expired Forgot Password Requests: {}", expiredRequests.size());
        forgotPasswordRequestRepository.deleteAll(expiredRequests);
    }
}
