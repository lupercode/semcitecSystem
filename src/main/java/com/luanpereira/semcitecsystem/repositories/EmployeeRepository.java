package com.luanpereira.semcitecsystem.repositories;

import com.luanpereira.semcitecsystem.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
}
