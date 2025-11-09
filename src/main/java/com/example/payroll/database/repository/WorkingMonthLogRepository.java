package com.example.payroll.database.repository;

import java.util.*;
import com.example.payroll.database.entity.WorkingMonthLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkingMonthLogRepository extends CrudRepository<WorkingMonthLog, Long> {

  Optional<WorkingMonthLog> findByEmployeeIdAndLogYearAndLogMonth(Long employeeId, Integer year, Integer month);

  List<WorkingMonthLog> findByEmployeeId(Long employeeId);

  List<WorkingMonthLog> findByEmployeeIdAndLogYear(Long employeeId, Integer year);

}
