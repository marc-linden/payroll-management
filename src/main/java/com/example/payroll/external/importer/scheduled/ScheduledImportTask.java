package com.example.payroll.external.importer.scheduled;

import java.util.concurrent.TimeUnit;
import com.example.payroll.external.importer.factory.FictionalWorkingMonthLogFactory;
import com.example.payroll.external.importer.service.SyncWorkingMonthLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledImportTask {
  private final SyncWorkingMonthLogService syncWorkingMonthLogService;

  @Scheduled(fixedRateString = "${scheduled.external.import.task.rate.millis}", timeUnit = TimeUnit.MILLISECONDS)
  public void importFromExternalTimeTrackingSystem() {
    syncWorkingMonthLogService.syncWorkingMonthLogs(new FictionalWorkingMonthLogFactory().createFictionalExternalWorkingMonthLogs());
  }
}
