package com.crio.learning_navigator.repository;

import com.crio.learning_navigator.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    Subject findByName(String subjectName);
    
}
