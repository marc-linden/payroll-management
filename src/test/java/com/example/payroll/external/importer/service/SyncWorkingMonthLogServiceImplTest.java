package com.example.payroll.external.importer.service;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.*;
import com.example.payroll.database.entity.WorkingLogSource;
import com.example.payroll.database.entity.WorkingMonthLog;
import com.example.payroll.database.repository.WorkingMonthLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SyncWorkingMonthLogServiceImplTest {
  @Mock
  private WorkingMonthLogRepository workingMonthLogRepository;

  private SyncWorkingMonthLogService testable;

  @BeforeEach
  void setUp() {
    this.testable = new SyncWorkingMonthLogServiceImpl(workingMonthLogRepository);
  }

  @Test
  void GIVEN_working_log_with_internal_source_THEN_no_invocation_of_repository() {
    // Arrange
    WorkingMonthLog workingMonthLog = mock(WorkingMonthLog.class);
    doReturn(WorkingLogSource.INTERNAL).when(workingMonthLog).getWorkingLogSource();

    // Act
    testable.syncWorkingMonthLogs(Collections.singletonList(workingMonthLog));

    // Assert
    verify(workingMonthLogRepository, never()).save(any());
  }

  @Test
  void GIVEN_external_working_log_exists_THEN_it_is_deleted() {
    // Arrange
    WorkingMonthLog workingMonthLog = mock(WorkingMonthLog.class);
    doReturn(WorkingLogSource.EXTERNAL_TIME_RECORDING).when(workingMonthLog).getWorkingLogSource();
    doReturn(Optional.of(workingMonthLog)).when(workingMonthLogRepository).findByEmployeeIdAndLogYearAndLogMonthAndWorkingLogSource(anyLong(), anyInt(), anyInt(), any(WorkingLogSource.class));

    // Act
    testable.syncWorkingMonthLogs(Collections.singletonList(workingMonthLog));

    // Assert
    verify(workingMonthLogRepository).delete(workingMonthLog);
  }

  @Test
  void GIVEN_external_working_log_does_not_exist_THEN_delete_is_not_invoked() {
    // Arrange
    WorkingMonthLog workingMonthLog = mock(WorkingMonthLog.class);
    doReturn(WorkingLogSource.EXTERNAL_TIME_RECORDING).when(workingMonthLog).getWorkingLogSource();
    doReturn(Optional.empty()).when(workingMonthLogRepository).findByEmployeeIdAndLogYearAndLogMonthAndWorkingLogSource(anyLong(), anyInt(), anyInt(), any(WorkingLogSource.class));

    // Act
    testable.syncWorkingMonthLogs(Collections.singletonList(workingMonthLog));

    // Assert
    verify(workingMonthLogRepository, never()).delete(workingMonthLog);
  }

  @Test
  void GIVEN_external_working_log_THEN_save_is_invoked() {
    // Arrange
    WorkingMonthLog workingMonthLog = mock(WorkingMonthLog.class);
    doReturn(WorkingLogSource.EXTERNAL_TIME_RECORDING).when(workingMonthLog).getWorkingLogSource();

    // Act
    testable.syncWorkingMonthLogs(Collections.singletonList(workingMonthLog));

    // Assert
    verify(workingMonthLogRepository).save(workingMonthLog);
  }
}
