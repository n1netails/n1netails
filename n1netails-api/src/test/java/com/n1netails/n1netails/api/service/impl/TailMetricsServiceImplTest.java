package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.model.entity.TailEntity;
import com.n1netails.n1netails.api.model.entity.TailLevelEntity;
import com.n1netails.n1netails.api.model.response.TailDatasetResponse;
import com.n1netails.n1netails.api.model.response.TailMonthlySummaryResponse;
import com.n1netails.n1netails.api.repository.TailLevelRepository;
import com.n1netails.n1netails.api.repository.TailRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TailMetricsServiceImplTest {

    @Mock
    private TailRepository tailRepository;

    @Mock
    private TailLevelRepository tailLevelRepository;

    @InjectMocks
    private TailMetricsServiceImpl tailMetricsService;

    private final DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.US);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private TailLevelEntity createLevel(Long id, String name) {
        TailLevelEntity level = new TailLevelEntity();
        level.setId(id);
        level.setName(name);
        return level;
    }

    private TailEntity createTail(Long id, TailLevelEntity level, int daysAgo) {
        TailEntity tail = new TailEntity();
        tail.setId(id);
        tail.setLevel(level);
        tail.setTimestamp(Instant.now().minus(Duration.ofDays(daysAgo)));
        // Other essential properties can be set if needed for the test logic
        return tail;
    }

    @Test
    void testGetTailMonthlySummary_Success() {
        // --- Mock TailLevelRepository ---
        TailLevelEntity infoLevel = createLevel(1L, "INFO");
        TailLevelEntity successLevel = createLevel(2L, "SUCCESS");
        TailLevelEntity warnLevel = createLevel(3L, "WARN");
        TailLevelEntity errorLevel = createLevel(4L, "ERROR");
        TailLevelEntity criticalLevel = createLevel(5L, "CRITICAL");
        TailLevelEntity customLevel1 = createLevel(6L, "CUSTOM_LOG");
        TailLevelEntity customLevel2 = createLevel(7L, "OTHER_APP_LOG");

        List<TailLevelEntity> allLevels = Arrays.asList(infoLevel, successLevel, warnLevel, errorLevel, criticalLevel, customLevel1, customLevel2);
        when(tailLevelRepository.findAll()).thenReturn(allLevels);

        // --- Mock TailRepository ---
        List<TailEntity> tails = new ArrayList<>();
        // Today
        tails.add(createTail(1L, infoLevel, 0));
        tails.add(createTail(2L, infoLevel, 0));
        tails.add(createTail(3L, successLevel, 0));
        tails.add(createTail(4L, customLevel1, 0));
        // 1 day ago
        tails.add(createTail(5L, warnLevel, 1));
        tails.add(createTail(6L, customLevel2, 1));
        // 5 days ago
        tails.add(createTail(7L, errorLevel, 5));
        tails.add(createTail(8L, criticalLevel, 5));
        tails.add(createTail(9L, infoLevel, 5));
        // 27 days ago
        tails.add(createTail(10L, successLevel, 27));
        tails.add(createTail(11L, customLevel1, 27));
        // A tail older than 28 days (should be excluded by service logic)
        tails.add(createTail(12L, infoLevel, 30));


        // The service calculates start/end date based on LocalDate.now().
        // We need to ensure the mocked findByTimestampBetween is called with appropriate Instant args.
        // For simplicity in this test, we'll mock `any(Instant.class), any(Instant.class)`
        // but in a real scenario, you might want to be more precise with argument matchers
        // if the exact timestamp boundaries are critical to the test logic itself.
        when(tailRepository.findByTimestampBetween(any(Instant.class), any(Instant.class)))
            .thenAnswer(invocation -> {
                Instant startArg = invocation.getArgument(0);
                Instant endArg = invocation.getArgument(1);
                return tails.stream()
                    .filter(t -> !t.getTimestamp().isBefore(startArg) && t.getTimestamp().isBefore(endArg))
                    .collect(Collectors.toList());
            });


        // --- Call the service method ---
        TailMonthlySummaryResponse response = tailMetricsService.getTailMonthlySummary();

        // --- Assertions ---
        assertNotNull(response);

        // Assert Labels
        List<String> expectedLabels = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 28; i++) {
            expectedLabels.add(today.minusDays(27 - i).format(dayFormatter));
        }
        assertEquals(expectedLabels, response.getLabels());
        assertEquals(28, response.getLabels().size());

        // Assert Datasets
        assertNotNull(response.getDatasets());
        assertEquals(6, response.getDatasets().size(), "Should be 6 datasets: INFO, SUCCESS, WARN, ERROR, CRITICAL, Kuda");

        // Expected order: INFO, SUCCESS, WARN, ERROR, CRITICAL, Kuda
        List<String> expectedDatasetOrder = Arrays.asList("INFO", "SUCCESS", "WARN", "ERROR", "CRITICAL", "Kuda");
        List<String> actualDatasetOrder = response.getDatasets().stream().map(TailDatasetResponse::getLabel).collect(Collectors.toList());
        assertEquals(expectedDatasetOrder, actualDatasetOrder);

        // --- Detailed Assertions for each dataset ---
        LocalDate localToday = LocalDate.now();
        DateTimeFormatter responseDateFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.US);

        // INFO
        TailDatasetResponse infoDataset = response.getDatasets().get(0);
        assertEquals("INFO", infoDataset.getLabel());
        assertEquals(28, infoDataset.getData().size());
        // Expected: 2 for today (day 27 of 0-27), 1 for 5 days ago (day 22)
        for (int i = 0; i < 28; i++) {
            String currentDateLabel = localToday.minusDays(27 - i).format(responseDateFormatter);
            int expectedCount = 0;
            if (currentDateLabel.equals(localToday.format(responseDateFormatter))) expectedCount = 2; // Today
            else if (currentDateLabel.equals(localToday.minusDays(5).format(responseDateFormatter))) expectedCount = 1; // 5 days ago
            assertEquals(expectedCount, infoDataset.getData().get(i), "Mismatch for INFO on " + currentDateLabel);
        }

        // SUCCESS
        TailDatasetResponse successDataset = response.getDatasets().get(1);
        assertEquals("SUCCESS", successDataset.getLabel());
        assertEquals(28, successDataset.getData().size());
        // Expected: 1 for today (day 27), 1 for 27 days ago (day 0)
        for (int i = 0; i < 28; i++) {
            String currentDateLabel = localToday.minusDays(27 - i).format(responseDateFormatter);
            int expectedCount = 0;
            if (currentDateLabel.equals(localToday.format(responseDateFormatter))) expectedCount = 1; // Today
            else if (currentDateLabel.equals(localToday.minusDays(27).format(responseDateFormatter))) expectedCount = 1; // 27 days ago
            assertEquals(expectedCount, successDataset.getData().get(i), "Mismatch for SUCCESS on " + currentDateLabel);
        }
        
        // WARN
        TailDatasetResponse warnDataset = response.getDatasets().get(2);
        assertEquals("WARN", warnDataset.getLabel());
        assertEquals(28, warnDataset.getData().size());
        // Expected: 1 for 1 day ago (day 26)
         for (int i = 0; i < 28; i++) {
            String currentDateLabel = localToday.minusDays(27 - i).format(responseDateFormatter);
            int expectedCount = 0;
            if (currentDateLabel.equals(localToday.minusDays(1).format(responseDateFormatter))) expectedCount = 1; // 1 day ago
            assertEquals(expectedCount, warnDataset.getData().get(i), "Mismatch for WARN on " + currentDateLabel);
        }

        // ERROR
        TailDatasetResponse errorDataset = response.getDatasets().get(3);
        assertEquals("ERROR", errorDataset.getLabel());
        assertEquals(28, errorDataset.getData().size());
        // Expected: 1 for 5 days ago (day 22)
        for (int i = 0; i < 28; i++) {
            String currentDateLabel = localToday.minusDays(27 - i).format(responseDateFormatter);
            int expectedCount = 0;
            if (currentDateLabel.equals(localToday.minusDays(5).format(responseDateFormatter))) expectedCount = 1; // 5 days ago
            assertEquals(expectedCount, errorDataset.getData().get(i), "Mismatch for ERROR on " + currentDateLabel);
        }

        // CRITICAL
        TailDatasetResponse criticalDataset = response.getDatasets().get(4);
        assertEquals("CRITICAL", criticalDataset.getLabel());
        assertEquals(28, criticalDataset.getData().size());
        // Expected: 1 for 5 days ago (day 22)
         for (int i = 0; i < 28; i++) {
            String currentDateLabel = localToday.minusDays(27 - i).format(responseDateFormatter);
            int expectedCount = 0;
            if (currentDateLabel.equals(localToday.minusDays(5).format(responseDateFormatter))) expectedCount = 1; // 5 days ago
            assertEquals(expectedCount, criticalDataset.getData().get(i), "Mismatch for CRITICAL on " + currentDateLabel);
        }

        // Kuda
        TailDatasetResponse kudaDataset = response.getDatasets().get(5);
        assertEquals("Kuda", kudaDataset.getLabel());
        assertEquals(28, kudaDataset.getData().size());
        // Expected: 1 (customLevel1) for today (day 27), 1 (customLevel2) for 1 day ago (day 26), 1 (customLevel1) for 27 days ago (day 0)
        for (int i = 0; i < 28; i++) {
            String currentDateLabel = localToday.minusDays(27 - i).format(responseDateFormatter);
            int expectedCount = 0;
            if (currentDateLabel.equals(localToday.format(responseDateFormatter))) expectedCount = 1; // customLevel1 today
            else if (currentDateLabel.equals(localToday.minusDays(1).format(responseDateFormatter))) expectedCount = 1; // customLevel2 1 day ago
            else if (currentDateLabel.equals(localToday.minusDays(27).format(responseDateFormatter))) expectedCount = 1; // customLevel1 27 days ago
            assertEquals(expectedCount, kudaDataset.getData().get(i), "Mismatch for Kuda on " + currentDateLabel + " (index " + i + ")");
        }
    }

    @Test
    void testGetTailMonthlySummary_NoTailsFound() {
        // --- Mock TailLevelRepository ---
        TailLevelEntity infoLevel = createLevel(1L, "INFO");
        TailLevelEntity successLevel = createLevel(2L, "SUCCESS");
        // Add a custom level to ensure Kuda dataset is created even with no tails
        TailLevelEntity customLevel = createLevel(3L, "CUSTOM");
        List<TailLevelEntity> allLevels = Arrays.asList(infoLevel, successLevel, customLevel);
        when(tailLevelRepository.findAll()).thenReturn(allLevels);

        // --- Mock TailRepository ---
        when(tailRepository.findByTimestampBetween(any(Instant.class), any(Instant.class)))
            .thenReturn(new ArrayList<>()); // Return empty list

        // --- Call the service method ---
        TailMonthlySummaryResponse response = tailMetricsService.getTailMonthlySummary();

        // --- Assertions ---
        assertNotNull(response);

        // Assert Labels
        List<String> expectedLabels = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 28; i++) {
            expectedLabels.add(today.minusDays(27 - i).format(dayFormatter));
        }
        assertEquals(expectedLabels, response.getLabels());
        assertEquals(28, response.getLabels().size());

        // Assert Datasets (INFO, SUCCESS, Kuda because customLevel was defined)
        assertNotNull(response.getDatasets());
        assertEquals(3, response.getDatasets().size(), "Should be 3 datasets: INFO, SUCCESS, Kuda");
        
        List<String> expectedDatasetOrder = Arrays.asList("INFO", "SUCCESS", "Kuda");
        List<String> actualDatasetOrder = response.getDatasets().stream().map(TailDatasetResponse::getLabel).collect(Collectors.toList());
        assertEquals(expectedDatasetOrder, actualDatasetOrder);

        for (TailDatasetResponse dataset : response.getDatasets()) {
            assertEquals(28, dataset.getData().size());
            for (int count : dataset.getData()) {
                assertEquals(0, count, "All data counts should be 0 for dataset " + dataset.getLabel());
            }
        }
    }

    @Test
    void testGetTailMonthlySummary_NoLevelsFoundAndNoTails() { // Renamed for clarity
        // --- Mock TailLevelRepository ---
        when(tailLevelRepository.findAll()).thenReturn(new ArrayList<>()); // No levels defined

        // --- Mock TailRepository ---
        // Return some tails, but they won't be categorized by standard levels
        // They should all fall into "Kuda" if Kuda is created when no levels are found.
        // However, the current implementation's Kuda relies on allLevels.stream() for non-standard.
        // If allLevels is empty, Kuda won't be created from non-standard levels.
        // Let's test with no tails as well, as it's a clearer "empty" state if no levels.
        when(tailRepository.findByTimestampBetween(any(Instant.class), any(Instant.class)))
            .thenReturn(new ArrayList<>());


        // --- Call the service method ---
        TailMonthlySummaryResponse response = tailMetricsService.getTailMonthlySummary();

        // --- Assertions ---
        assertNotNull(response);

        // Assert Labels
        List<String> expectedLabels = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 28; i++) {
            expectedLabels.add(today.minusDays(27 - i).format(dayFormatter));
        }
        assertEquals(expectedLabels, response.getLabels());
        assertEquals(28, response.getLabels().size());

        // Assert Datasets
        // Based on current service logic: if tailLevelRepository.findAll() is empty,
        // no standard levels are processed, and the Kuda aggregation loop also won't run.
        // The `hasKudaData || allLevels.stream().anyMatch(l -> !standardLevels.contains(l.getName()))`
        // condition for adding Kuda will be false. So, empty datasets.
        assertNotNull(response.getDatasets());
        assertTrue(response.getDatasets().isEmpty(), "Datasets should be empty when no TailLevelEntities are found");
    }


    @Test
    void testGetTailMonthlySummary_NoLevelsFoundButTailsExist() {
        // --- Mock TailLevelRepository ---
        when(tailLevelRepository.findAll()).thenReturn(new ArrayList<>()); // No levels defined

        // --- Mock TailRepository ---
        List<TailEntity> tails = new ArrayList<>();
        // These tails have levels, but these TailLevelEntity objects are not known by TailLevelRepository
        TailLevelEntity unknownLevel = createLevel(100L, "ORPHAN_LEVEL");
        tails.add(createTail(20L, unknownLevel, 1));
        tails.add(createTail(21L, unknownLevel, 2));

        when(tailRepository.findByTimestampBetween(any(Instant.class), any(Instant.class)))
            .thenAnswer(invocation -> tails); // Return the tails with unknown levels

        // --- Call the service method ---
        TailMonthlySummaryResponse response = tailMetricsService.getTailMonthlySummary();

        // --- Assertions ---
        assertNotNull(response);
        assertEquals(28, response.getLabels().size()); // Labels should still be there

        // Assert Datasets
        // With current implementation:
        // 1. Standard levels loop won't add anything as allLevels is empty.
        // 2. Kuda aggregation loop (`for (TailLevelEntity levelEntity : allLevels)`) also won't run.
        // 3. The condition `allLevels.stream().anyMatch(l -> !standardLevels.contains(l.getName()))` will be false.
        // 4. `kudaCounts` will remain empty. So `hasKudaData` will be false.
        // Thus, Kuda dataset is not created.
        assertNotNull(response.getDatasets());
        assertTrue(response.getDatasets().isEmpty(), "Datasets should be empty as no levels from repository means no classification, including Kuda, is performed based on iterating repository levels.");
    }


    @Test
    void testGetTailMonthlySummary_OnlyKudaLevelTails() {
        // --- Mock TailLevelRepository ---
        TailLevelEntity customLevel1 = createLevel(6L, "CUSTOM_LOG");
        TailLevelEntity customLevel2 = createLevel(7L, "OTHER_APP_LOG");
        List<TailLevelEntity> allLevels = Arrays.asList(customLevel1, customLevel2);
        when(tailLevelRepository.findAll()).thenReturn(allLevels);

        // --- Mock TailRepository ---
        List<TailEntity> tails = new ArrayList<>();
        tails.add(createTail(1L, customLevel1, 0)); // Today
        tails.add(createTail(2L, customLevel2, 1)); // Yesterday
        tails.add(createTail(3L, customLevel1, 27)); // 27 days ago

        when(tailRepository.findByTimestampBetween(any(Instant.class), any(Instant.class)))
            .thenReturn(tails);

        // --- Call the service method ---
        TailMonthlySummaryResponse response = tailMetricsService.getTailMonthlySummary();

        // --- Assertions ---
        assertNotNull(response);
        assertEquals(28, response.getLabels().size());
        assertNotNull(response.getDatasets());
        assertEquals(1, response.getDatasets().size(), "Should only be Kuda dataset");

        TailDatasetResponse kudaDataset = response.getDatasets().get(0);
        assertEquals("Kuda", kudaDataset.getLabel());
        assertEquals(28, kudaDataset.getData().size());

        LocalDate localToday = LocalDate.now();
        DateTimeFormatter responseDateFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.US);

        for (int i = 0; i < 28; i++) {
            String currentDateLabel = localToday.minusDays(27 - i).format(responseDateFormatter);
            int expectedCount = 0;
            if (currentDateLabel.equals(localToday.format(responseDateFormatter))) expectedCount = 1; // customLevel1 today
            else if (currentDateLabel.equals(localToday.minusDays(1).format(responseDateFormatter))) expectedCount = 1; // customLevel2 yesterday
            else if (currentDateLabel.equals(localToday.minusDays(27).format(responseDateFormatter))) expectedCount = 1; // customLevel1 27 days ago
            assertEquals(expectedCount, kudaDataset.getData().get(i), "Mismatch for Kuda on " + currentDateLabel);
        }
    }
}
