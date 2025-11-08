package com.example.payroll.exception;

public class EmployeeNotFoundException extends RuntimeException {

  public EmployeeNotFoundException(Long employeeId) {
    super(String.format("Could not find employee [id: %d]", employeeId));
  }
}
