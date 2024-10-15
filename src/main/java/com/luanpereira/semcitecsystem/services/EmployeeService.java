package com.luanpereira.semcitecsystem.services;

import com.luanpereira.semcitecsystem.models.Employee;
import com.luanpereira.semcitecsystem.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public Long countItem() {
        return employeeRepository.count();
    }

    public Optional<Employee> findById(UUID uuid) {
        return this.employeeRepository.findById(uuid);
    }

    public List<Employee> findAll() {
        return this.employeeRepository.findAll();
    }

    public Employee save(Employee employeeData) {
        if (employeeData.getUuid() != null) {
            Employee employee = this.employeeRepository.findById(employeeData.getUuid()).get();
            employeeData.setObs(employee.getObs());
        }
        return this.employeeRepository.save(employeeData);
    }
    
    public Employee saveDescription(Employee employeeData) {
        Employee employee = this.employeeRepository.findById(employeeData.getUuid()).get();
        employee.setObs(employeeData.getObs());
        return this.employeeRepository.save(employee);
    }
}
