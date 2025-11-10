package com.example.payroll.external.importer.service;

import java.util.*;
import java.util.function.Predicate;
import com.example.payroll.database.entity.WorkingLogSource;
import com.example.payroll.database.entity.WorkingMonthLog;
import com.example.payroll.database.repository.WorkingMonthLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SyncWorkingMonthLogServiceImpl implements SyncWorkingMonthLogService {
  private final WorkingMonthLogRepository workingMonthLogRepository;

  @Override
  public void syncWorkingMonthLogs(List<WorkingMonthLog> workingMonthLogsToSync) {
    log.info("Start syncing {} external working month logs", workingMonthLogsToSync.size());
    workingMonthLogsToSync.stream()
        .filter(hasExternalSource())
        .forEach(this::syncWorkingMonthLog);
    log.info("Syncing finished");
  }

  @Transactional
  void syncWorkingMonthLog(WorkingMonthLog workingMonthLogToReplace) {
    workingMonthLogRepository.findByEmployeeIdAndLogYearAndLogMonthAndWorkingLogSource(
            workingMonthLogToReplace.getEmployeeId(),
            workingMonthLogToReplace.getLogYear(),
            workingMonthLogToReplace.getLogMonth(),
            WorkingLogSource.EXTERNAL_TIME_RECORDING)
        .ifPresent(this::deleteWithTraceLog);
    saveWithTraceLog(workingMonthLogToReplace);
  }

  private void deleteWithTraceLog(WorkingMonthLog workingMonthLogToDelete) {
    log.trace("Deleting working month log {}", toString(workingMonthLogToDelete));
    workingMonthLogRepository.delete(workingMonthLogToDelete);
  }

  private void saveWithTraceLog(WorkingMonthLog workingMonthLogToSave) {
    log.trace("Saving working month log {}", toString(workingMonthLogToSave));
    workingMonthLogRepository.save(workingMonthLogToSave);
  }

  private Predicate<WorkingMonthLog> hasExternalSource() {
    return workingMonthLog -> WorkingLogSource.EXTERNAL_TIME_RECORDING.equals(workingMonthLog.getWorkingLogSource());
  }

  private String toString(WorkingMonthLog workingMonthLog) {
    return String.format("[employeeId: %s, year: %d, month: %d]", workingMonthLog.getEmployeeId(), workingMonthLog.getLogYear(), workingMonthLog.getLogMonth());
  }
}
