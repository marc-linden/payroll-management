package com.example.payroll.web.api;

import com.example.payroll.database.entity.Employee;
import com.example.payroll.database.entity.WorkingMonthLog;
import com.example.payroll.database.repository.EmployeeRepository;
import com.example.payroll.database.repository.WorkingMonthLogRepository;
import com.example.payroll.exception.EmployeeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payroll")
@RequiredArgsConstructor
public class WorkingMonthLogController {
  private final EmployeeRepository employeeRepository;
  private final WorkingMonthLogRepository workingMonthLogRepository;

  @GetMapping("/employees/{id}/working-month-log")
  public WorkingMonthLog getWorkingMonthLog(@PathVariable(name = "id") Long employeeId, @RequestParam Integer year, @RequestParam Integer month) {
    Employee workLogEmployee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

    return null;
  }
}
