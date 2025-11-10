package com.example.payroll.security;

import com.example.payroll.database.entity.Employee;

public interface SecurityContextService {

  Employee getAuthenticatedEmployee();

}
