package com.example.payroll.external.importer.factory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import java.util.*;
import com.example.payroll.database.entity.WorkingLogSource;
import com.example.payroll.database.entity.WorkingMonthLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FictionalWorkingMonthLogFactoryTest {
  private FictionalWorkingMonthLogFactory testable;

  @BeforeEach
  void setUp() {
    testable = new FictionalWorkingMonthLogFactory();
  }

  @Test
  void GIVEN_create_is_called_THEN_a_list_of_30_logs_are_returned() {
    // Act
    List<WorkingMonthLog> fictionalExternalWorkingMonthLogs = testable.createFictionalExternalWorkingMonthLogs();

    // Assert
    assertThat(fictionalExternalWorkingMonthLogs, hasSize(30));
  }

  @Test
  void GIVEN_create_is_called_THEN_all_worklogs_are_within_the_boundaries_of_existing_demo_employees() {
    // Act
    List<WorkingMonthLog> fictionalExternalWorkingMonthLogs = testable.createFictionalExternalWorkingMonthLogs();

    // Assert
    for (WorkingMonthLog workingMonthLog : fictionalExternalWorkingMonthLogs) {
      assertThat(workingMonthLog.getEmployeeId(), greaterThanOrEqualTo(4002L));
      assertThat(workingMonthLog.getEmployeeId(), lessThanOrEqualTo(4004L));
    }
  }

  @Test
  void GIVEN_create_is_called_THEN_all_worklogs_have_fixed_year() {
    // Act
    List<WorkingMonthLog> fictionalExternalWorkingMonthLogs = testable.createFictionalExternalWorkingMonthLogs();

    // Assert
    for (WorkingMonthLog workingMonthLog : fictionalExternalWorkingMonthLogs) {
      assertThat(workingMonthLog.getLogYear(), is(2023));
    }
  }

  @Test
  void GIVEN_create_is_called_THEN_all_worklogs_are_have_a_month_value_within_expected_boundaries() {
    // Act
    List<WorkingMonthLog> fictionalExternalWorkingMonthLogs = testable.createFictionalExternalWorkingMonthLogs();

    // Assert
    for (WorkingMonthLog workingMonthLog : fictionalExternalWorkingMonthLogs) {
      assertThat(workingMonthLog.getLogMonth(), greaterThanOrEqualTo(0));
      assertThat(workingMonthLog.getLogMonth(), lessThanOrEqualTo(11));
    }
  }

  @Test
  void GIVEN_create_is_called_THEN_each_worklog_has_insert_employee_equal_to_working_log_employee() {
    // Act
    List<WorkingMonthLog> fictionalExternalWorkingMonthLogs = testable.createFictionalExternalWorkingMonthLogs();

    // Assert
    for (WorkingMonthLog workingMonthLog : fictionalExternalWorkingMonthLogs) {
      assertThat(workingMonthLog.getInsertEmployeeId(), equalTo(workingMonthLog.getEmployeeId()));
    }
  }

  @Test
  void GIVEN_create_is_called_THEN_each_worklog_has_source_type_external() {
    // Act
    List<WorkingMonthLog> fictionalExternalWorkingMonthLogs = testable.createFictionalExternalWorkingMonthLogs();

    // Assert
    for (WorkingMonthLog workingMonthLog : fictionalExternalWorkingMonthLogs) {
      assertThat(workingMonthLog.getWorkingLogSource(), is(WorkingLogSource.EXTERNAL_TIME_RECORDING));
    }
  }

  @Test
  void GIVEN_create_is_called_THEN_working_log_time_value_is_within_expected_boundaries() {
    // Act
    List<WorkingMonthLog> fictionalExternalWorkingMonthLogs = testable.createFictionalExternalWorkingMonthLogs();

    // Assert
    for (WorkingMonthLog workingMonthLog : fictionalExternalWorkingMonthLogs) {
      assertThat(workingMonthLog.getLogTimeInHours(), greaterThanOrEqualTo(0));
      assertThat(workingMonthLog.getLogTimeInHours(), lessThanOrEqualTo(53));
    }
  }
}
