package com.example.interview_simulator.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import com.example.interview_simulator.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
}
