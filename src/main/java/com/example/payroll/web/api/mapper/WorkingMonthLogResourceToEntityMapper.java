package com.example.payroll.web.api.mapper;

import com.example.payroll.database.entity.WorkingMonthLog;
import com.example.payroll.web.api.model.WorkingMonthLogResource;
import org.springframework.stereotype.Component;

@Component
public class WorkingMonthLogResourceToEntityMapper implements ResourceToEntityMapper<WorkingMonthLogResource, WorkingMonthLog> {
  @Override
  public WorkingMonthLog fromResource(WorkingMonthLogResource resource) {
    WorkingMonthLog workingMonthLog = new WorkingMonthLog();
    workingMonthLog.setEmployeeId(resource.getEmployeeId());
    workingMonthLog.setInsertEmployeeId(resource.getInsertEmployeeId());
    workingMonthLog.setInsertTimestamp(resource.getInsertTimestamp());
    workingMonthLog.setLogYear(resource.getYear());
    workingMonthLog.setLogMonth(resource.getMonth());
    workingMonthLog.setLogTimeInHours(resource.getWorkingHours());
    workingMonthLog.setWorkingLogSource(resource.getWorkingLogSource());
    return workingMonthLog;
  }

  @Override
  public WorkingMonthLogResource toResource(WorkingMonthLog entity) {
    return WorkingMonthLogResource.builder()
        .employeeId(entity.getEmployeeId())
        .insertEmployeeId(entity.getInsertEmployeeId())
        .insertTimestamp(entity.getInsertTimestamp())
        .workingLogSource(entity.getWorkingLogSource())
        .year(entity.getLogYear())
        .month(entity.getLogMonth())
        .workingHours(entity.getLogTimeInHours())
        .build();
  }
}
