package com.example.payroll.database.service;

import java.util.*;
import com.example.payroll.database.entity.WorkingMonthLog;
import com.example.payroll.web.api.model.WorkingMonthLogResource;

public interface WorkingMonthLogApiService {

  void ensureExistingEmployee(Long employeeId);

  WorkingMonthLog create(WorkingMonthLogResource workingMonthLogResource);

  WorkingMonthLog update(WorkingMonthLogResource workingMonthLogResource);

  void deleteByEmployeeIdAndYearAndMonth(Long employeeId, Integer year, Integer month);

  WorkingMonthLog findMandatoryWorkingMonthLog(Long employeeId, Integer year, Integer month);

  Optional<WorkingMonthLog> findByEmployeeIdAndYearAndMonth(Long employeeId, Integer year, Integer month);

  List<WorkingMonthLog> findByEmployeeIdAndYear(Long employeeId, Integer year);

  List<WorkingMonthLog> findByEmployeeId(Long employeeId);
}
