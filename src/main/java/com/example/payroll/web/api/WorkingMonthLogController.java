package com.example.payroll.web.api;

import static java.util.function.Predicate.not;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.*;
import java.util.function.Predicate;
import com.example.payroll.database.entity.WorkingMonthLog;
import com.example.payroll.database.service.WorkingMonthLogApiCrudSupport;
import com.example.payroll.exception.InconsistentRequestDataException;
import com.example.payroll.web.api.mapper.ResourceModelMapper;
import com.example.payroll.web.api.model.WorkingMonthLogResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "WorkingMonthLogs", description = "Employee working month management APIs")
@RequiredArgsConstructor
public class WorkingMonthLogController {
  private final WorkingMonthLogApiCrudSupport workingMonthLogApiCrudSupport;
  private final ResourceModelMapper<WorkingMonthLogResource, WorkingMonthLog> resourceToEntityMapper;

  @GetMapping("/employees/{id}/working-month-log")
  @Operation(summary = "Get all working month logs of an employee by employee ID", description = "Returns all working month logs")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
      @ApiResponse(responseCode = "404", description = "Could not find employee [id: {id}]"),
      @ApiResponse(responseCode = "404", description = "Could not find any working month log")
  })
  public CollectionModel<EntityModel<WorkingMonthLogResource>> all(@PathVariable(name = "id") Long employeeId) {
    workingMonthLogApiCrudSupport.ensureExistingEmployee(employeeId);
    List<EntityModel<WorkingMonthLogResource>> workingLogsOfEmployee = workingMonthLogApiCrudSupport.findByEmployeeId(employeeId).stream()
        .map(resourceToEntityMapper::toHalEntityModel)
        .toList();
    return CollectionModel.of(workingLogsOfEmployee, linkTo(methodOn(WorkingMonthLogController.class).all(employeeId)).withSelfRel());
  }

  @GetMapping("/employees/{id}/working-month-log/{year}")
  @Operation(summary = "Get all working month logs of a year for employee by employee ID and year", description = "Returns all working month logs per year")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
      @ApiResponse(responseCode = "404", description = "Could not find employee [id: {id}]"),
      @ApiResponse(responseCode = "404", description = "Could not find any working month log")
  })
  public CollectionModel<EntityModel<WorkingMonthLogResource>> allOfYear(@PathVariable(name = "id") Long employeeId, @PathVariable Integer year) {
    workingMonthLogApiCrudSupport.ensureExistingEmployee(employeeId);
    List<EntityModel<WorkingMonthLogResource>> workingLogsOfYear = workingMonthLogApiCrudSupport.findByEmployeeIdAndYear(employeeId, year).stream()
        .map(resourceToEntityMapper::toHalEntityModel)
        .toList();
    return CollectionModel.of(workingLogsOfYear, linkTo(methodOn(WorkingMonthLogController.class).allOfYear(employeeId, year)).withSelfRel());
  }

  @GetMapping("/employees/{id}/working-month-log/{year}/{month}")
  @Operation(summary = "Get a single internal working month log for an employee by employee ID, year and month", description = "Returns a single working month log")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
      @ApiResponse(responseCode = "404", description = "Could not find employee [id: {id}]"),
      @ApiResponse(responseCode = "404", description = "Could not find working month log")
  })
  public EntityModel<WorkingMonthLogResource> single(@PathVariable(name = "id") Long employeeId, @PathVariable Integer year, @PathVariable Integer month) {
    workingMonthLogApiCrudSupport.ensureExistingEmployee(employeeId);
    return resourceToEntityMapper.toHalEntityModel(workingMonthLogApiCrudSupport.findMandatoryWorkingMonthLog(employeeId, year, month));
  }

  @PostMapping("/employees/{id}/working-month-log/{year}/{month}")
  @Operation(summary = "Create new internal working month log for an employee by employee ID, year and month", description = "Create a single working month log")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Successfully created"),
      @ApiResponse(responseCode = "400", description = "Validation failed"),
      @ApiResponse(responseCode = "400", description = "Validation failed"),
      @ApiResponse(responseCode = "404", description = "Could not find employee [id: {id}]"),
      @ApiResponse(responseCode = "409", description = "Working log exists already")
  })
  public ResponseEntity<EntityModel<WorkingMonthLogResource>> newWorkingMonthLog(
      @PathVariable(name = "id") Long employeeId,
      @PathVariable Integer year,
      @PathVariable Integer month,
      @RequestBody @Valid WorkingMonthLogResource workingMonthLogResource) {

    workingMonthLogApiCrudSupport.ensureExistingEmployee(employeeId);
    ensureConsistencyWithPathVariables(employeeId, year, month, workingMonthLogResource);
    WorkingMonthLog workingMonthLogCreated = workingMonthLogApiCrudSupport.create(workingMonthLogResource);

    EntityModel<WorkingMonthLogResource> entityModel = resourceToEntityMapper.toHalEntityModel(workingMonthLogCreated);
    return ResponseEntity
        .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
        .body(entityModel);
  }

  @PutMapping("/employees/{id}/working-month-log/{year}/{month}")
  @Operation(summary = "Update existing internal working month log for an employee by employee ID, year and month", description = "Update a single existing working month log")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Successfully updated"),
      @ApiResponse(responseCode = "400", description = "Validation failed"),
      @ApiResponse(responseCode = "400", description = "Path variables do not match to the request body"),
      @ApiResponse(responseCode = "404", description = "Could not find employee [id: {id}]"),
      @ApiResponse(responseCode = "404", description = "Could not find working month log"),
  })
  public ResponseEntity<EntityModel<WorkingMonthLogResource>> replaceWorkingMonthLog(
      @PathVariable(name = "id") Long employeeId,
      @PathVariable Integer year,
      @PathVariable Integer month,
      @RequestBody @Valid WorkingMonthLogResource newResource) {

    workingMonthLogApiCrudSupport.ensureExistingEmployee(employeeId);
    ensureConsistencyWithPathVariables(employeeId, year, month, newResource);
    WorkingMonthLog workingMonthLogToUpdate = workingMonthLogApiCrudSupport.update(newResource);

    EntityModel<WorkingMonthLogResource> entityModel = resourceToEntityMapper.toHalEntityModel(workingMonthLogToUpdate);
    return ResponseEntity
        .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
        .body(entityModel);
  }

  @DeleteMapping("/employees/{id}/working-month-log/{year}/{month}")
  @Operation(summary = "Delete existing internal working month log for an employee by employee ID, year and month", description = "Delete a single existing working month log")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Successfully deleted"),
      @ApiResponse(responseCode = "404", description = "Could not find employee [id: {id}]"),
      @ApiResponse(responseCode = "404", description = "Could not find working month log"),
  })
  ResponseEntity<Void> deleteWorkingMonthLog(
      @PathVariable(name = "id") Long employeeId,
      @PathVariable Integer year,
      @PathVariable Integer month) {

    workingMonthLogApiCrudSupport.ensureExistingEmployee(employeeId);
    workingMonthLogApiCrudSupport.deleteByEmployeeIdAndYearAndMonth(employeeId, year, month);
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
