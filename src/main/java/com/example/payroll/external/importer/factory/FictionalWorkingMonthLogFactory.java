package com.example.payroll.external.importer.factory;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import com.example.payroll.database.entity.WorkingLogSource;
import com.example.payroll.database.entity.WorkingMonthLog;

public class FictionalWorkingMonthLogFactory {

  public List<WorkingMonthLog> createFictionalExternalWorkingMonthLogs() {
    return IntStream.range(0, 30)
        .mapToObj(number -> createFictionalExternalWorkingMonthLog())
        .toList();
  }

  private WorkingMonthLog createFictionalExternalWorkingMonthLog() {
    Long employeeId = getRandomExistingEmployeeId();
    WorkingMonthLog externalWorkingMonthLog = new WorkingMonthLog();
    externalWorkingMonthLog.setWorkingLogSource(WorkingLogSource.EXTERNAL_TIME_RECORDING);
    externalWorkingMonthLog.setInsertEmployeeId(employeeId);
    externalWorkingMonthLog.setInsertTimestamp(Instant.now());
    externalWorkingMonthLog.setEmployeeId(employeeId);
    externalWorkingMonthLog.setLogYear(2023); // fixed year
    externalWorkingMonthLog.setLogMonth(getRandomMonth());
    externalWorkingMonthLog.setLogTimeInHours(getRandomWorkingTimeInHours());
    return externalWorkingMonthLog;
  }

  private long getRandomExistingEmployeeId() {
    return ThreadLocalRandom.current().nextLong(4002, 4005);
  }

  private int getRandomMonth() {
    return ThreadLocalRandom.current().nextInt(0, 12);
  }

  private int getRandomWorkingTimeInHours() {
    return ThreadLocalRandom.current().nextInt(0, 54);
  }
}
