package com.sagax.shop.service;

import com.sagax.shop.model.entity.Employee;
import com.sagax.shop.repository.DepartmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;


    @Transactional
    public void addEmployeeToDepartment(Long id, List<Employee> employees) {
        departmentRepository.findById(id)
                .ifPresent(department -> department.employees().addAll(employees));
    }
}
