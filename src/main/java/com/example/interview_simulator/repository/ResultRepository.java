package com.example.interview_simulator.repository;

import com.example.interview_simulator.entity.Result;
import com.example.interview_simulator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResultRepository extends JpaRepository<Result, Integer> {

    List<Result> findByUser(User user);

    List<Result> findByUserOrderByAttemptNumberAsc(User user); // ✅ for score history

    List<Result> findAllByOrderByScoreDesc();
}