package com.example.interview_simulator.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import com.example.interview_simulator.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
}
