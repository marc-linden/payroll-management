package com.example.payroll.web.api;

import static java.util.function.Predicate.not;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.*;
import java.util.function.Predicate;
import com.example.payroll.database.entity.WorkingMonthLog;
import com.example.payroll.database.service.WorkingMonthLogApiService;
import com.example.payroll.exception.InconsistentRequestDataException;
import com.example.payroll.web.api.mapper.ResourceModelMapper;
import com.example.payroll.web.api.model.WorkingMonthLogResource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payroll")
@RequiredArgsConstructor
public class WorkingMonthLogController {
  private final WorkingMonthLogApiService workingMonthLogApiService;
  private final ResourceModelMapper<WorkingMonthLogResource, WorkingMonthLog> resourceToEntityMapper;

  @GetMapping("/employees/{id}/working-month-log/{year}/{month}")
  public EntityModel<WorkingMonthLogResource> single(@PathVariable(name = "id") Long employeeId, @PathVariable Integer year, @PathVariable Integer month) {
    workingMonthLogApiService.ensureExistingEmployee(employeeId);
    return resourceToEntityMapper.toHalEntityModel(workingMonthLogApiService.findMandatoryWorkingMonthLog(employeeId, year, month));
  }

  @GetMapping("/employees/{id}/working-month-log/{year}")
  public CollectionModel<EntityModel<WorkingMonthLogResource>> allOfYear(@PathVariable(name = "id") Long employeeId, @PathVariable Integer year) {
    workingMonthLogApiService.ensureExistingEmployee(employeeId);
    List<EntityModel<WorkingMonthLogResource>> workingLogsOfYear = workingMonthLogApiService.findByEmployeeIdAndYear(employeeId, year).stream()
        .map(resourceToEntityMapper::toHalEntityModel)
        .toList();
    return CollectionModel.of(workingLogsOfYear, linkTo(methodOn(WorkingMonthLogController.class).allOfYear(employeeId, year)).withSelfRel());
  }

  @GetMapping("/employees/{id}/working-month-log")
  public CollectionModel<EntityModel<WorkingMonthLogResource>> all(@PathVariable(name = "id") Long employeeId) {
    workingMonthLogApiService.ensureExistingEmployee(employeeId);
    List<EntityModel<WorkingMonthLogResource>> workingLogsOfEmployee = workingMonthLogApiService.findByEmployeeId(employeeId).stream()
        .map(resourceToEntityMapper::toHalEntityModel)
        .toList();
    return CollectionModel.of(workingLogsOfEmployee, linkTo(methodOn(WorkingMonthLogController.class).all(employeeId)).withSelfRel());
  }

  @PostMapping("/employees/{id}/working-month-log/{year}/{month}")
  public ResponseEntity<EntityModel<WorkingMonthLogResource>> newWorkingMonthLog(
      @PathVariable(name = "id") Long employeeId,
      @PathVariable Integer year,
      @PathVariable Integer month,
      @RequestBody @Valid WorkingMonthLogResource workingMonthLogResource) {

    workingMonthLogApiService.ensureExistingEmployee(employeeId);
    ensureConsistencyWithPathVariables(employeeId, year, month, workingMonthLogResource);
    WorkingMonthLog workingMonthLogCreated = workingMonthLogApiService.create(workingMonthLogResource);

    EntityModel<WorkingMonthLogResource> entityModel = resourceToEntityMapper.toHalEntityModel(workingMonthLogCreated);
    return ResponseEntity
        .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
        .body(entityModel);
  }

  @PutMapping("/employees/{id}/working-month-log/{year}/{month}")
  public ResponseEntity<EntityModel<WorkingMonthLogResource>> replaceWorkingMonthLog(
      @PathVariable(name = "id") Long employeeId,
      @PathVariable Integer year,
      @PathVariable Integer month,
      @RequestBody @Valid WorkingMonthLogResource newResource) {

    workingMonthLogApiService.ensureExistingEmployee(employeeId);
    ensureConsistencyWithPathVariables(employeeId, year, month, newResource);
    WorkingMonthLog workingMonthLogToUpdate = workingMonthLogApiService.update(newResource);

    EntityModel<WorkingMonthLogResource> entityModel = resourceToEntityMapper.toHalEntityModel(workingMonthLogToUpdate);
    return ResponseEntity
        .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
        .body(entityModel);
  }

  @DeleteMapping("/employees/{id}/working-month-log/{year}/{month}")
  ResponseEntity<Void> deleteWorkingMonthLog(
      @PathVariable(name = "id") Long employeeId,
      @PathVariable Integer year,
      @PathVariable Integer month) {

    workingMonthLogApiService.ensureExistingEmployee(employeeId);
    workingMonthLogApiService.deleteByEmployeeIdAndYearAndMonth(employeeId, year, month);
    return ResponseEntity.noContent().build();
  }

  private void ensureConsistencyWithPathVariables(Long employeeId, Integer year, Integer month, WorkingMonthLogResource workingMonthLog) {
    if (not(hasEqualValues(employeeId, year, month)).test(workingMonthLog)) {
      throw new InconsistentRequestDataException("Path variables do not match to the request body");
    }
  }

  private Predicate<WorkingMonthLogResource> hasEqualValues(Long employeeId, Integer year, Integer month) {
    return workingMonthLogResource ->
        employeeId.equals(workingMonthLogResource.getEmployeeId()) &&
            year.equals(workingMonthLogResource.getYear()) &&
            month.equals(workingMonthLogResource.getMonth());
  }
}
