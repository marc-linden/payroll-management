package com.example.payroll.database.service;

import java.time.Instant;
import java.util.*;
import com.example.payroll.database.entity.WorkingLogSource;
import com.example.payroll.database.entity.WorkingMonthLog;
import com.example.payroll.database.repository.EmployeeRepository;
import com.example.payroll.database.repository.WorkingMonthLogRepository;
import com.example.payroll.exception.EmployeeNotFoundException;
import com.example.payroll.exception.ResourceAlreadyExistsException;
import com.example.payroll.exception.ResourceNotFoundException;
import com.example.payroll.security.SecurityContextService;
import com.example.payroll.web.api.mapper.ResourceModelMapper;
import com.example.payroll.web.api.model.WorkingMonthLogResource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkingMonthLogApiCrudSupportImpl implements WorkingMonthLogApiCrudSupport {
  private final SecurityContextService securityContextService;
  private final EmployeeRepository employeeRepository;
  private final WorkingMonthLogRepository workingMonthLogRepository;
  private final ResourceModelMapper<WorkingMonthLogResource, WorkingMonthLog> resourceToEntityMapper;
  private final EntityManager entityManager;

  private static final String WORKING_LOG_EXISTS_ALREADY = "Working log exists already";

  @Override
  public void ensureExistingEmployee(Long employeeId) {
    employeeRepository.findById(employeeId)
        .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
  }

  @Override
  @Transactional
  public WorkingMonthLog create(WorkingMonthLogResource resource) {
    try {
      if (findByEmployeeIdAndYearAndMonth(resource.getEmployeeId(), resource.getYear(), resource.getMonth()).isPresent()) {
        throw new ResourceAlreadyExistsException(WORKING_LOG_EXISTS_ALREADY);
      }
      WorkingMonthLog workingMonthLog = resourceToEntityMapper.toJpaEntity(resource);
      workingMonthLog.setInsertEmployeeId(securityContextService.getAuthenticatedEmployee().getId());
      workingMonthLog.setInsertTimestamp(Instant.now());
      workingMonthLog.setWorkingLogSource(WorkingLogSource.INTERNAL);

      WorkingMonthLog saved = workingMonthLogRepository.save(workingMonthLog);
      entityManager.flush(); // force immediate flush to database to check for the constraint violation
      log.debug("Created working month log: {}", saved);

      return saved;
    } catch (DataIntegrityViolationException | PersistenceException e) {
      log.warn("Duplicate resource creation attempt: [{}]", e.getClass().getSimpleName());
      throw new ResourceAlreadyExistsException(WORKING_LOG_EXISTS_ALREADY);
    }
  }

  @Override
  @Transactional
  public WorkingMonthLog update(WorkingMonthLogResource resource) {
    WorkingMonthLog workingMonthLogToUpdate = findMandatoryWorkingMonthLog(resource.getEmployeeId(), resource.getYear(), resource.getMonth());
    workingMonthLogToUpdate.setInsertEmployeeId(securityContextService.getAuthenticatedEmployee().getId());
    workingMonthLogToUpdate.setInsertTimestamp(Instant.now());
    workingMonthLogToUpdate.setLogTimeInHours(resource.getWorkingHours());

    WorkingMonthLog saved = workingMonthLogRepository.save(workingMonthLogToUpdate);
    entityManager.flush(); // force immediate flush to database
    log.debug("Updated working month log: {}", saved);

    return saved;
  }

  @Override
  @Transactional
  public void deleteByEmployeeIdAndYearAndMonth(Long employeeId, Integer year, Integer month) {
    WorkingMonthLog workingMonthLogToDelete = findMandatoryWorkingMonthLog(employeeId, year, month);
    workingMonthLogRepository.delete(workingMonthLogToDelete);
    log.debug("Deleted working month log: {}", workingMonthLogToDelete);
  }

  @Override
  public Optional<WorkingMonthLog> findByEmployeeIdAndYearAndMonth(Long employeeId, Integer year, Integer month) {
    return workingMonthLogRepository.findByEmployeeIdAndLogYearAndLogMonthAndWorkingLogSource(employeeId, year, month, WorkingLogSource.INTERNAL);
  }

  @Override
  public List<WorkingMonthLog> findByEmployeeIdAndYear(Long employeeId, Integer year) {
    return workingMonthLogRepository.findByEmployeeIdAndLogYear(employeeId, year);
  }

  @Override
  public List<WorkingMonthLog> findByEmployeeId(Long employeeId) {
    return workingMonthLogRepository.findByEmployeeId(employeeId);
  }

  @Override
  public WorkingMonthLog findMandatoryWorkingMonthLog(Long employeeId, Integer year, Integer month) {
    return findByEmployeeIdAndYearAndMonth(employeeId, year, month)
        .orElseThrow(() -> new ResourceNotFoundException("Could not find working month log"));
  }
}
