package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.entity.TailEntity;
import com.n1netails.n1netails.api.model.entity.TailLevelEntity;
import com.n1netails.n1netails.api.model.entity.TailStatusEntity;
import com.n1netails.n1netails.api.model.entity.TailTypeEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class TailRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TailRepository tailRepository;

    private TailLevelEntity defaultLevel;
    private TailTypeEntity defaultType;
    private TailStatusEntity defaultStatus;

    @BeforeEach
    void setUp() {
        defaultLevel = new TailLevelEntity();
        defaultLevel.setName("Test Level"); // Assuming a 'name' field or similar
        entityManager.persist(defaultLevel);

        defaultType = new TailTypeEntity();
        defaultType.setName("Test Type"); // Assuming a 'name' field or similar
        entityManager.persist(defaultType);

        defaultStatus = new TailStatusEntity();
        defaultStatus.setName("Test Status"); // Assuming a 'name' field or similar
        entityManager.persist(defaultStatus);
        entityManager.flush();
    }

    private TailEntity createTailEntityWithTimestamp(Instant timestamp) {
        TailEntity tail = new TailEntity();
        tail.setTitle("Test Tail");
        tail.setTimestamp(timestamp);
        tail.setLevel(defaultLevel);
        tail.setType(defaultType);
        tail.setStatus(defaultStatus);
        // Set other mandatory fields if any, based on TailEntity structure
        // For example, if 'description' or 'details' were non-nullable
        tail.setDescription("Test Description");
        tail.setDetails("Test Details");
        return tail;
    }

    private Instant getStartOfDay() {
        return LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC);
    }

    private Instant getEndOfDay() {
        return LocalDate.now().atTime(LocalTime.MAX).toInstant(ZoneOffset.UTC);
    }

    private Instant getYesterdayStartOfDay() {
        return LocalDate.now().minusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
    }

    private Instant getTomorrowStartOfDay() {
        return LocalDate.now().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
    }

    // Tests for findByTimestampBetween

    @Test
    void findByTimestampBetween_whenNoEntitiesExist_shouldReturnEmptyList() {
        // Arrange - nothing to persist for this specific test

        // Act
        List<TailEntity> foundEntities = tailRepository.findByTimestampBetween(getStartOfDay(), getEndOfDay());

        // Assert
        assertThat(foundEntities).isEmpty();
    }

    @Test
    void findByTimestampBetween_whenOneEntityExistsForCurrentDay_shouldReturnListWithOneEntity() {
        // Arrange
        Instant timestampInCurrentDay = getStartOfDay().plusSeconds(3600); // One hour into the current day
        TailEntity matchingEntity = createTailEntityWithTimestamp(timestampInCurrentDay);
        entityManager.persist(matchingEntity);
        entityManager.flush();

        // Act
        List<TailEntity> foundEntities = tailRepository.findByTimestampBetween(getStartOfDay(), getEndOfDay());

        // Assert
        assertThat(foundEntities).hasSize(1);
        assertThat(foundEntities.get(0).getId()).isEqualTo(matchingEntity.getId());
        assertThat(foundEntities.get(0).getTimestamp()).isBetween(getStartOfDay(), getEndOfDay());
    }

    @Test
    void findByTimestampBetween_whenMultipleEntitiesExistForCurrentDay_shouldReturnListWithAllMatchingEntities() {
        // Arrange
        Instant timestamp1 = getStartOfDay().plusSeconds(1000);
        Instant timestamp2 = getEndOfDay().minusSeconds(1000);
        TailEntity matchingEntity1 = createTailEntityWithTimestamp(timestamp1);
        TailEntity matchingEntity2 = createTailEntityWithTimestamp(timestamp2);
        TailEntity nonMatchingEntityYesterday = createTailEntityWithTimestamp(getYesterdayStartOfDay());
        TailEntity nonMatchingEntityTomorrow = createTailEntityWithTimestamp(getTomorrowStartOfDay());


        entityManager.persist(matchingEntity1);
        entityManager.persist(matchingEntity2);
        entityManager.persist(nonMatchingEntityYesterday);
        entityManager.persist(nonMatchingEntityTomorrow);
        entityManager.flush();

        // Act
        List<TailEntity> foundEntities = tailRepository.findByTimestampBetween(getStartOfDay(), getEndOfDay());

        // Assert
        assertThat(foundEntities).hasSize(2);
        assertThat(foundEntities).extracting(TailEntity::getId).containsExactlyInAnyOrder(matchingEntity1.getId(), matchingEntity2.getId());
    }

    @Test
    void findByTimestampBetween_whenEntitiesExistButNotForCurrentDay_shouldReturnEmptyList() {
        // Arrange
        TailEntity entityYesterday = createTailEntityWithTimestamp(getYesterdayStartOfDay());
        TailEntity entityTomorrow = createTailEntityWithTimestamp(getTomorrowStartOfDay());
        entityManager.persist(entityYesterday);
        entityManager.persist(entityTomorrow);
        entityManager.flush();

        // Act
        List<TailEntity> foundEntities = tailRepository.findByTimestampBetween(getStartOfDay(), getEndOfDay());

        // Assert
        assertThat(foundEntities).isEmpty();
    }

    // Tests for countByTimestampBetween

    @Test
    void countByTimestampBetween_whenNoEntitiesExist_shouldReturnZero() {
        // Arrange - nothing to persist

        // Act
        long count = tailRepository.countByTimestampBetween(getStartOfDay(), getEndOfDay());

        // Assert
        assertThat(count).isZero();
    }
    
    @Test
    void countByTimestampBetween_whenZeroEntitiesMatchCurrentDay_shouldReturnZero() {
        // Arrange
        entityManager.persist(createTailEntityWithTimestamp(getYesterdayStartOfDay()));
        entityManager.persist(createTailEntityWithTimestamp(getTomorrowStartOfDay()));
        entityManager.flush();

        // Act
        long count = tailRepository.countByTimestampBetween(getStartOfDay(), getEndOfDay());

        // Assert
        assertThat(count).isZero();
    }


    @Test
    void countByTimestampBetween_whenOneEntityMatchesCurrentDay_shouldReturnOne() {
        // Arrange
        Instant timestampInCurrentDay = getStartOfDay().plusSeconds(7200); // Two hours into the current day
        entityManager.persist(createTailEntityWithTimestamp(timestampInCurrentDay));
        entityManager.persist(createTailEntityWithTimestamp(getYesterdayStartOfDay())); // non-matching
        entityManager.flush();

        // Act
        long count = tailRepository.countByTimestampBetween(getStartOfDay(), getEndOfDay());

        // Assert
        assertThat(count).isEqualTo(1);
    }

    @Test
    void countByTimestampBetween_whenMultipleEntitiesMatchCurrentDay_shouldReturnCorrectCount() {
        // Arrange
        entityManager.persist(createTailEntityWithTimestamp(getStartOfDay().plusSeconds(1)));
        entityManager.persist(createTailEntityWithTimestamp(getEndOfDay().minusSeconds(1)));
        entityManager.persist(createTailEntityWithTimestamp(Instant.now())); // Should be within start/end of day
        entityManager.persist(createTailEntityWithTimestamp(getYesterdayStartOfDay())); // non-matching
        entityManager.persist(createTailEntityWithTimestamp(getTomorrowStartOfDay())); // non-matching
        entityManager.flush();

        // Act
        long count = tailRepository.countByTimestampBetween(getStartOfDay(), getEndOfDay());

        // Assert
        assertThat(count).isEqualTo(3);
    }
}
