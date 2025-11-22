package com.crio.learning_navigator.repository;

import com.crio.learning_navigator.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    @Query("select (count(s) > 0) from Subject s where upper(s.name) = upper(?1)")
    boolean existsByNameIgnoreCase(String name);
    
}
