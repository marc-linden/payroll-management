package com.example.payroll.web.api;

import static java.util.function.Predicate.not;

import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;
import com.example.payroll.database.entity.WorkingLogSource;
import com.example.payroll.database.entity.WorkingMonthLog;
import com.example.payroll.database.repository.EmployeeRepository;
import com.example.payroll.database.repository.WorkingMonthLogRepository;
import com.example.payroll.exception.EmployeeNotFoundException;
import com.example.payroll.exception.InconsistentRequestDataException;
import com.example.payroll.exception.ResourceAlreadyExistsException;
import com.example.payroll.exception.ResourceNotFoundException;
import com.example.payroll.security.SecurityContextService;
import com.example.payroll.web.api.mapper.ResourceToEntityMapper;
import com.example.payroll.web.api.model.WorkingMonthLogResource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
  private final SecurityContextService securityContextService;
  private final EmployeeRepository employeeRepository;
  private final WorkingMonthLogRepository workingMonthLogRepository;
  private final ResourceToEntityMapper<WorkingMonthLogResource, WorkingMonthLog> resourceToEntityMapper;

  @GetMapping("/employees/{id}/working-month-log/{year}/{month}")
  public WorkingMonthLogResource single(@PathVariable(name = "id") Long employeeId, @PathVariable Integer year, @PathVariable Integer month) {
    ensureExistingEmployee(employeeId);
    return resourceToEntityMapper.toResource(findMandatoryWorkingMonthLog(employeeId, year, month));
  }

  @GetMapping("/employees/{id}/working-month-log/{year}")
  public List<WorkingMonthLogResource> allPerYear(@PathVariable(name = "id") Long employeeId, @PathVariable Integer year) {
    ensureExistingEmployee(employeeId);
    return workingMonthLogRepository.findByEmployeeIdAndLogYear(employeeId, year).stream()
        .map(resourceToEntityMapper::toResource)
        .toList();
  }

  @GetMapping("/employees/{id}/working-month-log")
  public List<WorkingMonthLogResource> all(@PathVariable(name = "id") Long employeeId) {
    ensureExistingEmployee(employeeId);
    return workingMonthLogRepository.findByEmployeeId(employeeId).stream()
        .map(resourceToEntityMapper::toResource)
        .toList();
  }

  @PostMapping("/employees/{id}/working-month-log/{year}/{month}")
  public WorkingMonthLogResource newWorkingMonthLog(
      @PathVariable(name = "id") Long employeeId,
      @PathVariable Integer year,
      @PathVariable Integer month,
      @RequestBody @Valid WorkingMonthLogResource workingMonthLogResource) {

    ensureExistingEmployee(employeeId);
    ensureConsistencyWithPathVariables(employeeId, year, month, workingMonthLogResource);

    if (workingMonthLogRepository.findByEmployeeIdAndLogYearAndLogMonth(employeeId, year, month).isPresent()) {
      throw new ResourceAlreadyExistsException("Working log exists already");
    }

    WorkingMonthLog workingMonthLog = resourceToEntityMapper.fromResource(workingMonthLogResource);
    workingMonthLog.setInsertEmployeeId(securityContextService.getAuthenticatedEmployee().getId());
    workingMonthLog.setInsertTimestamp(Instant.now());
    workingMonthLog.setWorkingLogSource(WorkingLogSource.INTERNAL);

    return resourceToEntityMapper.toResource(workingMonthLogRepository.save(workingMonthLog));
  }

  @PutMapping("/employees/{id}/working-month-log/{year}/{month}")
  public WorkingMonthLogResource replaceWorkingMonthLog(
      @PathVariable(name = "id") Long employeeId,
      @PathVariable Integer year,
      @PathVariable Integer month,
      @RequestBody @Valid WorkingMonthLogResource newResource) {

    ensureExistingEmployee(employeeId);
    ensureConsistencyWithPathVariables(employeeId, year, month, newResource);

    WorkingMonthLog workingMonthLog = findMandatoryWorkingMonthLog(employeeId, year, month);
    workingMonthLog.setInsertEmployeeId(securityContextService.getAuthenticatedEmployee().getId());
    workingMonthLog.setInsertTimestamp(Instant.now());

    return resourceToEntityMapper.toResource(workingMonthLogRepository.save(workingMonthLog));
  }

  @DeleteMapping("/employees/{id}/working-month-log/{year}/{month}")
  void deleteWorkingMonthLog(
      @PathVariable(name = "id") Long employeeId,
      @PathVariable Integer year,
      @PathVariable Integer month) {

    ensureExistingEmployee(employeeId);
    workingMonthLogRepository.delete(findMandatoryWorkingMonthLog(employeeId, year, month));
  }

  private void ensureExistingEmployee(Long employeeId) {
    employeeRepository.findById(employeeId)
        .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
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

  private WorkingMonthLog findMandatoryWorkingMonthLog(Long employeeId, Integer year, Integer month) {
    return workingMonthLogRepository.findByEmployeeIdAndLogYearAndLogMonth(employeeId, year, month)
        .orElseThrow(() -> new ResourceNotFoundException("Could not find working month log"));
  }
}
