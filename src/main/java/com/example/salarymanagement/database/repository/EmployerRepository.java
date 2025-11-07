package com.example.salarymanagement.database.repository;

import com.example.salarymanagement.database.entity.Employer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployerRepository extends CrudRepository<Employer, Long> {
}
