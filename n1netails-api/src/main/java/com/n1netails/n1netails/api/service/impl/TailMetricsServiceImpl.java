package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.dto.TailLevelAndTimestamp;
import com.n1netails.n1netails.api.model.dto.TailTimestampAndResolvedTimestamp;
import com.n1netails.n1netails.api.model.entity.OrganizationEntity;
import com.n1netails.n1netails.api.model.entity.TailEntity;
import com.n1netails.n1netails.api.model.entity.TailLevelEntity;
import com.n1netails.n1netails.api.model.response.*;
import com.n1netails.n1netails.api.model.entity.UsersEntity; // Assuming TailEntity has a direct User field
import com.n1netails.n1netails.api.repository.TailLevelRepository;
import com.n1netails.n1netails.api.repository.TailRepository;
import com.n1netails.n1netails.api.service.TailMetricsService;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
// import java.time.LocalDate; // Already imported

@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("tailMetricsService")
public class TailMetricsServiceImpl implements TailMetricsService {

    private static final String N1NETAILS_ORGANIZATION_NAME = "n1netails";
    private final TailRepository tailRepository;
    private final TailLevelRepository tailLevelRepository;

    // private Specification<TailEntity> getAuthSpecification(UserPrincipal currentUser) { // Unused
    //     Set<Long> userOrgIds = currentUser.getOrganizations().stream()
    //             .map(OrganizationEntity::getId)
    //             .collect(Collectors.toSet());
    //
    //     if (userOrgIds.isEmpty()) {
    //         return (root, query, cb) -> cb.disjunction();
    //     }
    //
    //     boolean isN1neTailsOrgMember = currentUser.getOrganizations().stream()
    //             .anyMatch(org -> N1NETAILS_ORGANIZATION_NAME.equals(org.getName()));
    //
    //     return (root, query, cb) -> {
    //         Predicate orgPredicate = root.get("organization").get("id").in(userOrgIds);
    //         if (isN1neTailsOrgMember) {
    //             Predicate userPredicate = cb.equal(root.get("user").get("id"), currentUser.getId());
    //             return cb.and(orgPredicate, userPredicate);
    //         }
    //         return orgPredicate;
    //     };
    // }

    private Set<Long> getUserOrganizationIds(UserPrincipal currentUser) {
        return currentUser.getOrganizations().stream()
            .map(OrganizationEntity::getId)
            .collect(Collectors.toSet());
    }

    private boolean isN1neTailsMember(UserPrincipal currentUser) {
        return currentUser.getOrganizations().stream()
            .anyMatch(org -> N1NETAILS_ORGANIZATION_NAME.equals(org.getName()));
    }


    @Override
    public List<TailResponse> tailAlertsToday(String timezoneIdString, UserPrincipal currentUser) {
        ZoneId userZone = ZoneId.of(timezoneIdString);
        Instant startOfDayUserTzAsUtc = getStartOfDayInUTC(userZone);
        Instant endOfDayUserTzAsUtc = getEndOfDayInUTC(userZone);

        Set<Long> orgIds = getUserOrganizationIds(currentUser);
        if (orgIds.isEmpty()) return Collections.emptyList();
        Long userIdForN1neTails = isN1neTailsMember(currentUser) ? currentUser.getId() : null;

        List<TailEntity> tailEntities = tailRepository.findByTimestampBetweenAndOrganizationIdsAndOptionalUserId(
                startOfDayUserTzAsUtc, endOfDayUserTzAsUtc, orgIds, userIdForN1neTails);

        return tailEntities.stream()
                .map(this::mapToTailResponse)
                .collect(Collectors.toList());
    }

    @Override
    public long countAlertsToday(String timezoneIdString, UserPrincipal currentUser) {
        ZoneId userZone = ZoneId.of(timezoneIdString);
        Instant startOfDayUserTzAsUtc = getStartOfDayInUTC(userZone);
        Instant endOfDayUserTzAsUtc = getEndOfDayInUTC(userZone);

        Set<Long> orgIds = getUserOrganizationIds(currentUser);
        if (orgIds.isEmpty()) return 0;
        Long userIdForN1neTails = isN1neTailsMember(currentUser) ? currentUser.getId() : null;

        return tailRepository.countByTimestampBetweenAndOrganizationIdsAndOptionalUserId(
                startOfDayUserTzAsUtc, endOfDayUserTzAsUtc, orgIds, userIdForN1neTails);
    }

    @Override
    public List<TailResponse> tailAlertsResolved(UserPrincipal currentUser) {
        Set<Long> orgIds = getUserOrganizationIds(currentUser);
        if (orgIds.isEmpty()) return Collections.emptyList();
        Long userIdForN1neTails = isN1neTailsMember(currentUser) ? currentUser.getId() : null;

        List<TailEntity> tailEntities = tailRepository.findAllByStatusNameAndOrganizationIdsAndOptionalUserId(
                "RESOLVED", orgIds, userIdForN1neTails);
        return tailEntities.stream()
                .map(this::mapToTailResponse)
                .collect(Collectors.toList());
    }

    @Override
    public long countAlertsResolved(UserPrincipal currentUser) {
        Set<Long> orgIds = getUserOrganizationIds(currentUser);
        if (orgIds.isEmpty()) return 0;
        Long userIdForN1neTails = isN1neTailsMember(currentUser) ? currentUser.getId() : null;

        return tailRepository.countByStatusNameAndOrganizationIdsAndOptionalUserId(
                "RESOLVED", orgIds, userIdForN1neTails);
    }

    @Override
    public List<TailResponse> tailAlertsNotResolved(UserPrincipal currentUser) {
        Set<Long> orgIds = getUserOrganizationIds(currentUser);
        if (orgIds.isEmpty()) return Collections.emptyList();
        Long userIdForN1neTails = isN1neTailsMember(currentUser) ? currentUser.getId() : null;

        List<TailEntity> tailEntities = tailRepository.findAllByStatusNameNotAndOrganizationIdsAndOptionalUserId(
                "RESOLVED", orgIds, userIdForN1neTails);
        return tailEntities.stream()
                .map(this::mapToTailResponse)
                .collect(Collectors.toList());
    }

    @Override
    public long countAlertsNotResolved(UserPrincipal currentUser) {
        Set<Long> orgIds = getUserOrganizationIds(currentUser);
        if (orgIds.isEmpty()) return 0;
        Long userIdForN1neTails = isN1neTailsMember(currentUser) ? currentUser.getId() : null;

        return tailRepository.countByStatusNameNotAndOrganizationIdsAndOptionalUserId(
                "RESOLVED", orgIds, userIdForN1neTails);
    }

    @Override
    public long tailAlertsMTTR(UserPrincipal currentUser) {
        Set<Long> orgIds = getUserOrganizationIds(currentUser);
        if (orgIds.isEmpty()) return 0;
        Long userIdForN1neTails = isN1neTailsMember(currentUser) ? currentUser.getId() : null;

        List<TailTimestampAndResolvedTimestamp> resolvedTimestampList =
            tailRepository.findTimestampsForMTTRCalculation(orgIds, userIdForN1neTails);

        if (resolvedTimestampList.isEmpty()) {
            return 0;
        }

        long totalDurationInSeconds = 0;
        for (TailTimestampAndResolvedTimestamp resolvedTimestamp : resolvedTimestampList) {
            Duration duration = Duration.between(resolvedTimestamp.getTimestamp(), resolvedTimestamp.getResolvedTimestamp());
            totalDurationInSeconds += duration.getSeconds();
        }
        return totalDurationInSeconds / resolvedTimestampList.size();
    }

    @Override
    public TailAlertsPerHourResponse getTailAlertsPerHour(String timezoneIdString, UserPrincipal currentUser) {
        ZoneId userZone = ZoneId.of(timezoneIdString);
        ZonedDateTime userZonedNow = ZonedDateTime.now(userZone);
        Instant nowUtc = userZonedNow.toInstant();
        Instant nineHoursAgoUtc = nowUtc.minus(9, ChronoUnit.HOURS);

        Set<Long> orgIds = getUserOrganizationIds(currentUser);
        Long userIdForN1neTails = isN1neTailsMember(currentUser) ? currentUser.getId() : null;

        List<Instant> timestamps;
        if (orgIds.isEmpty()) {
            timestamps = Collections.emptyList();
        } else {
            timestamps = tailRepository.findTimestampsBetweenForHourly(
                nineHoursAgoUtc, nowUtc, orgIds, userIdForN1neTails);
        }

        List<Integer> data = new ArrayList<>(Collections.nCopies(10, 0));
        List<String> labels = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:00").withZone(userZone);

        for (int i = 9; i >= 0; i--) {
            ZonedDateTime labelTime = userZonedNow.minusHours(i).truncatedTo(ChronoUnit.HOURS);
            labels.add(formatter.format(labelTime));
        }

        Instant hourlyBucketStartUtc = nineHoursAgoUtc.truncatedTo(ChronoUnit.HOURS);
        for (Instant timestamp : timestamps) {
            long hoursDifference = ChronoUnit.HOURS.between(hourlyBucketStartUtc, timestamp.truncatedTo(ChronoUnit.HOURS));
            int index = (int) hoursDifference;
            if (index >= 0 && index < data.size()) {
                data.set(index, data.get(index) + 1);
            }
        }
        return new TailAlertsPerHourResponse(labels, data);
    }

    @Override
    public TailMonthlySummaryResponse getTailMonthlySummary(String timezoneIdString, UserPrincipal currentUser) {
        ZoneId userZone = ZoneId.of(timezoneIdString);
        LocalDate today = LocalDate.now(userZone);
        List<String> labels = new ArrayList<>();
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.US);

        for (int i = 0; i < 29; i++) {
            labels.add(today.minusDays(28 - i).format(dayFormatter));
        }

        Instant endDate = today.plusDays(1).atStartOfDay().atZone(userZone).toInstant();
        Instant startDate = today.minusDays(28).atStartOfDay().atZone(userZone).toInstant();

        Set<Long> orgIds = getUserOrganizationIds(currentUser);
        Long userIdForN1neTails = isN1neTailsMember(currentUser) ? currentUser.getId() : null;

        List<TailLevelAndTimestamp> tailLevelAndTimestampList;
        if (orgIds.isEmpty()) {
            tailLevelAndTimestampList = Collections.emptyList();
        } else {
            tailLevelAndTimestampList = tailRepository.findLevelAndTimestampsBetweenForMonthlySummary(
                startDate, endDate, orgIds, userIdForN1neTails);
        }

        List<TailLevelEntity> allLevels = tailLevelRepository.findAll(); // This doesn't need filtering by user

        Map<String, Map<String, Integer>> levelDateCounts = new HashMap<>();
        DateTimeFormatter tailDateFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.US).withZone(userZone); // Use userZone

        for (TailLevelAndTimestamp tail: tailLevelAndTimestampList) {
            if (tail.getTimestamp() != null && tail.getLevel() != null && tail.getLevel().getName() != null) {
                // Convert tail timestamp to user's timezone before formatting
                String tailDateStr = tail.getTimestamp().atZone(userZone).format(tailDateFormatter);
                String levelName = tail.getLevel().getName();
                levelDateCounts.computeIfAbsent(levelName, k -> new HashMap<>())
                        .merge(tailDateStr, 1, Integer::sum);
            }
        }

        List<TailDatasetResponse> datasets = new ArrayList<>();
        List<String> standardLevels = Arrays.asList("INFO", "SUCCESS", "WARN", "ERROR", "CRITICAL");
        Map<String, Integer> kudaCounts = new HashMap<>();

        for (TailLevelEntity levelEntity : allLevels) {
            String levelName = levelEntity.getName();
            if (standardLevels.contains(levelName)) {
                List<Integer> levelData = new ArrayList<>();
                for (String label : labels) {
                    levelData.add(levelDateCounts.getOrDefault(levelName, Collections.emptyMap()).getOrDefault(label, 0));
                }
                datasets.add(new TailDatasetResponse(levelName, levelData));
            } else {
                for (String label : labels) {
                    kudaCounts.merge(label, levelDateCounts.getOrDefault(levelName, Collections.emptyMap()).getOrDefault(label, 0), Integer::sum);
                }
            }
        }

        List<Integer> kudaData = new ArrayList<>();
        boolean hasKudaData = false;
        for (String label : labels) {
            int count = kudaCounts.getOrDefault(label, 0);
            kudaData.add(count);
            if (count > 0) hasKudaData = true;
        }
        if (hasKudaData || allLevels.stream().anyMatch(l -> !standardLevels.contains(l.getName()))) {
            datasets.add(new TailDatasetResponse("Kuda", kudaData));
        }

        datasets.sort(Comparator.comparingInt(ds -> {
            switch (ds.getLabel()) {
                case "INFO": return 0;
                case "SUCCESS": return 1;
                case "WARN": return 2;
                case "ERROR": return 3;
                case "CRITICAL": return 4;
                case "Kuda": return 5;
                default: return 6;
            }
        }));
        return new TailMonthlySummaryResponse(labels, datasets);
    }

    @Override
    public TailDatasetMttrResponse getTailMTTRLast7Days(UserPrincipal currentUser) {
        Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);

        Set<Long> orgIds = getUserOrganizationIds(currentUser);
        Long userIdForN1neTails = isN1neTailsMember(currentUser) ? currentUser.getId() : null;

        List<TailTimestampAndResolvedTimestamp> recentTails;
        if (orgIds.isEmpty()) {
            recentTails = Collections.emptyList();
        } else {
            recentTails = tailRepository.findTimestampsForMTTRLast7Days(
                sevenDaysAgo, orgIds, userIdForN1neTails);
        }

        Map<LocalDate, List<Duration>> dailyResolutionTimes = new HashMap<>();
        // Use a consistent zone, e.g., UTC, for internal calculations to avoid DST issues with LocalDate keys
        ZoneId calculationZone = ZoneOffset.UTC;

        for (TailTimestampAndResolvedTimestamp tail : recentTails) {
            LocalDate creationDay = tail.getTimestamp().atZone(calculationZone).toLocalDate();
             // Ensure the tail's creation day is within the 7-day window relative to today in the calculationZone
            if (!creationDay.isBefore(LocalDate.now(calculationZone).minusDays(7))) {
                 Duration resolutionDuration = Duration.between(tail.getTimestamp(), tail.getResolvedTimestamp());
                 dailyResolutionTimes.computeIfAbsent(creationDay, k -> new ArrayList<>()).add(resolutionDuration);
            }
        }

        List<String> labels = new ArrayList<>();
        List<Double> data = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd", Locale.US);
        LocalDate todayInCalcZone = LocalDate.now(calculationZone);

        for (int i = 6; i >= 0; i--) {
            LocalDate day = todayInCalcZone.minusDays(i);
            labels.add(day.format(dateFormatter)); // Label should represent the day

            List<Duration> durationsForDay = dailyResolutionTimes.getOrDefault(day, Collections.emptyList());
            if (durationsForDay.isEmpty()) {
                data.add(0.0);
            } else {
                double totalSeconds = durationsForDay.stream().mapToLong(Duration::getSeconds).sum();
                double averageMttrHours = (totalSeconds / (double) durationsForDay.size()) / 3600.0;
                data.add(Math.round(averageMttrHours * 100.0) / 100.0);
            }
        }
        return new TailDatasetMttrResponse(labels, data);
    }

    private TailResponse mapToTailResponse(TailEntity entity) {
        if (entity == null) return null;
        TailResponse response = new TailResponse();
        response.setId(entity.getId());
        response.setTitle(entity.getTitle());
        response.setDescription(entity.getDescription());
        response.setTimestamp(entity.getTimestamp());
        response.setResolvedTimestamp(entity.getResolvedTimestamp());
        response.setDetails(entity.getDetails());
        if (entity.getLevel() != null) response.setLevel(entity.getLevel().getName());
        if (entity.getType() != null) response.setType(entity.getType().getName());
        if (entity.getStatus() != null) response.setStatus(entity.getStatus().getName());
        response.setAssignedUserId(entity.getAssignedUserId());
        // Note: assignedUsername, metadata, userId (owner), organizationId are not set here
        // This mapToTailResponse is specific to metrics, where these details might not be needed for TailResponse
        // If they are needed, this mapping should be more comprehensive or use the one from TailServiceImpl.
        return response;
    }

    private Instant getStartOfDayInUTC(ZoneId zoneId) {
        return LocalDate.now(zoneId).atStartOfDay(zoneId).toInstant();
    }

    private Instant getEndOfDayInUTC(ZoneId zoneId) {
        return LocalDate.now(zoneId).atTime(LocalTime.MAX).atZone(zoneId).toInstant();
    }

    // These seem unused, can be removed if so.
    // private Instant getYesterdayStartOfDay() {
    //     return LocalDate.now().minusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
    // }

    // private Instant getTomorrowStartOfDay() {
    //     return LocalDate.now().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
    // }
}
