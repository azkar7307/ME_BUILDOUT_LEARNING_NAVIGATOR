package com.crio.learning_navigator.repository;

import com.crio.learning_navigator.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    
}
