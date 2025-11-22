package com.crio.learning_navigator.repository;

import java.util.Optional;
import com.crio.learning_navigator.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    @Query("SELECT e FROM Exam e JOIN FETCH e.subject WHERE e.id = :id")
    Optional<Exam> findByIdWithSubject(Long id);

    @Query("select e from Exam e where upper(e.subject.name) = upper(?1)")
    Exam findExamBySubjecName(String subjectName);
    
}
