package com.example.salarymanagement.database.repository;

import com.example.salarymanagement.database.entity.WorkingMonthLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkingMonthLogRepository extends CrudRepository<WorkingMonthLog, Long> {
}
