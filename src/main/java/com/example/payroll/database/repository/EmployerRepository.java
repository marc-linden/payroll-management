package com.example.payroll.database.repository;

import com.example.payroll.database.entity.Employer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployerRepository extends CrudRepository<Employer, Long> {
}
