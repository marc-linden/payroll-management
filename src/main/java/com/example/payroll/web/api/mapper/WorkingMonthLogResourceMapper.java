package com.example.payroll.web.api.mapper;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.example.payroll.database.entity.WorkingMonthLog;
import com.example.payroll.web.api.WorkingMonthLogController;
import com.example.payroll.web.api.model.WorkingMonthLogResource;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

@Component
public class WorkingMonthLogResourceMapper implements ResourceModelMapper<WorkingMonthLogResource, WorkingMonthLog> {
  @Override
  public WorkingMonthLog toJpaEntity(WorkingMonthLogResource resource) {
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
  public EntityModel<WorkingMonthLogResource> toHalEntityModel(WorkingMonthLog entity) {
    return EntityModel.of(fromJpaEntityToResource(entity),
        linkTo(methodOn(WorkingMonthLogController.class).single(entity.getEmployeeId(), entity.getLogYear(), entity.getLogMonth())).withSelfRel(),
        linkTo(methodOn(WorkingMonthLogController.class).allOfYear(entity.getEmployeeId(), entity.getLogYear())).withRel("allOfYear"),
        linkTo(methodOn(WorkingMonthLogController.class).all(entity.getEmployeeId())).withRel("all"));
  }

  private WorkingMonthLogResource fromJpaEntityToResource(WorkingMonthLog entity) {
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
