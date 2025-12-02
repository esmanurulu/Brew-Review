package com.brewandreview.repository;

import com.brewandreview.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Bu satır sayesinde veritabanında kullanıcı adıyla arama yapabilirsin
    Employee findByUsername(String username);
}