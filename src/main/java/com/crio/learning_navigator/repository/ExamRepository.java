package com.crio.learning_navigator.repository;

import com.crio.learning_navigator.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExamRepository extends JpaRepository<Exam, Long> {

    @Query("select e from Exam e where upper(e.subject.name) = upper(?1)")
    Exam findExamBySubjecName(String anyString);
    
}
