package com.example.payroll.security;

import com.example.payroll.database.entity.Employee;
import com.example.payroll.database.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityContextServiceImpl implements SecurityContextService {
  private final EmployeeRepository employeeRepository;

  @Override
  public Employee getAuthenticatedEmployee() {
    // hard coded API user for the demo
    return employeeRepository.findById(4004L).orElseThrow(IllegalStateException::new);
  }
}
