package com.example.payroll.database.repository;

import com.example.payroll.database.entity.WorkingMonthLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkingMonthLogRepository extends CrudRepository<WorkingMonthLog, Long> {

  WorkingMonthLog findByEmployeeIdAndYearAndMonth(Long employeeId, Integer year, Integer month);
}
