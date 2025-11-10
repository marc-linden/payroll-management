package com.example.payroll;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import com.example.payroll.database.entity.WorkingLogSource;
import com.example.payroll.database.entity.WorkingMonthLog;
import com.example.payroll.database.repository.WorkingMonthLogRepository;
import com.example.payroll.external.importer.service.SyncWorkingMonthLogService;
import com.example.payroll.web.api.model.WorkingMonthLogResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "scheduled.external.import.enabled=false"
})
class ConcurrentExecutionOfApiAndImportTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private SyncWorkingMonthLogService syncWorkingMonthLogService;

  @Autowired
  private WorkingMonthLogRepository repository;

  @BeforeEach
  void setUp() {
    // swipe the table to have a deterministic state
    repository.deleteAll();
  }

  @Test
  void testConcurrentOperations() {
    // Arrange
    WorkingMonthLog workingMonthLog = new WorkingMonthLog();
    workingMonthLog.setWorkingLogSource(WorkingLogSource.EXTERNAL_TIME_RECORDING);
    workingMonthLog.setInsertEmployeeId(4002L);
    workingMonthLog.setInsertTimestamp(Instant.now());
    workingMonthLog.setEmployeeId(4002L);
    workingMonthLog.setLogYear(2023);
    workingMonthLog.setLogMonth(10);
    workingMonthLog.setLogTimeInHours(37);

    WorkingMonthLogResource resource = WorkingMonthLogResource.builder()
        .employeeId(4002L)
        .insertEmployeeId(4004L)
        .insertTimestamp(Instant.now())
        .year(2023)
        .month(10)
        .workingLogSource(WorkingLogSource.INTERNAL)
        .workingHours(36)
        .build();


    // Act - Run both operations concurrently
    CompletableFuture<ResponseEntity<String>> postFuture =
        CompletableFuture.supplyAsync(() ->
            restTemplate.postForEntity("/api/v1/payroll/employees/4002/working-month-log/2023/10", resource, String.class)
        );

    CompletableFuture<Void> importFuture =
        CompletableFuture.runAsync(() ->
            syncWorkingMonthLogService.syncWorkingMonthLogs(Collections.singletonList(workingMonthLog))
        );

    // Wait for both to complete
    CompletableFuture.allOf(postFuture, importFuture).join();

    // Assert
    ResponseEntity<String> response = postFuture.join();

    List<WorkingMonthLog> resources = repository.findByEmployeeIdAndLogYear(4002L, 2023);
    assertThat(resources, hasSize(2));

    // Verify response is CREATED
    assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
  }
}
