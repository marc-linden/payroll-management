package com.example.payroll;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.oneOf;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import com.example.payroll.database.entity.WorkingLogSource;
import com.example.payroll.database.entity.WorkingMonthLog;
import com.example.payroll.database.repository.WorkingMonthLogRepository;
import com.example.payroll.web.api.model.WorkingMonthLogResource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "scheduled.external.import.enabled=false"
})
class ConcurrentExecutionOfApiPostRequestsTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private WorkingMonthLogRepository repository;

  @Test
  void testConcurrentOperations() {
    // Arrange
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
    CompletableFuture<ResponseEntity<EntityModel>> postFutureOne =
        CompletableFuture.supplyAsync(() ->
            restTemplate.postForEntity("/api/v1/payroll/employees/4002/working-month-log/2023/10", resource, EntityModel.class)
        );

    CompletableFuture<ResponseEntity<EntityModel>> postFutureTwo =
        CompletableFuture.supplyAsync(() ->
            restTemplate.postForEntity("/api/v1/payroll/employees/4002/working-month-log/2023/10", resource, EntityModel.class)
        );

    // Wait for both to complete
    CompletableFuture.allOf(postFutureOne, postFutureTwo).join();

    // Assert
    List<WorkingMonthLog> resources = repository.findByEmployeeIdAndLogYear(4002L, 2023);
    assertThat(resources, hasSize(1));

    // Verify response is either CREATED or CONFLICT
    assertThat(postFutureOne.join().getStatusCode(), is(oneOf(HttpStatus.CREATED, HttpStatus.CONFLICT)));
    assertThat(postFutureTwo.join().getStatusCode(), is(oneOf(HttpStatus.CREATED, HttpStatus.CONFLICT)));
  }
}
