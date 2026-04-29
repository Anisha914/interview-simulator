package com.example.interview_simulator.controller;

import com.example.interview_simulator.entity.Question;
import com.example.interview_simulator.entity.Result;
import com.example.interview_simulator.entity.User;
import com.example.interview_simulator.repository.QuestionRepository;
import com.example.interview_simulator.repository.ResultRepository;
import com.example.interview_simulator.repository.UserRepository;
import com.example.interview_simulator.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.LinkedHashMap;

@RestController
@CrossOrigin
public class QuizController {

    @Autowired
    private ResultRepository resultRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private QuestionRepository questionRepo;

    @Autowired
    private EvaluationService evaluationService;

    // ================= LOGIN =================
    @PostMapping("/login")
    public String login(@RequestBody User user){
        User existing = userRepo.findByUsername(user.getUsername());
        if(existing == null){
            userRepo.save(user);
            return "User registered";
        } else {
            if(existing.getPassword().equals(user.getPassword())){
                return "Login success";
            } else {
                return "Invalid password";
            }
        }
    }

    // ================= SUBMIT =================
    @PostMapping("/submit")
    public String submit(@RequestBody SubmissionRequest req){

        System.out.println("Username received: " + req.getUsername());

        User user = userRepo.findByUsername(req.getUsername());

        System.out.println("User from DB: " + user);

        if(user == null){
            return "User not found";
        }

        // Keyword-based scoring
        int totalScore = 0;
        for(Answer a : req.getAnswers()){
            Optional<Question> optQ = questionRepo.findById(a.getQid());
            if(optQ.isPresent()){
                Question q = optQ.get();
                if(q.getKeywords() != null){
                    totalScore += evaluationService.evaluateAnswer(a.getAnswer(), q.getKeywords());
                }
            }
        }

        // ✅ Find how many attempts this user already has
        List<Result> existing = resultRepo.findByUser(user);
        int nextAttempt = existing.stream()
                .mapToInt(Result::getAttemptNumber)
                .max()
                .orElse(0) + 1;

        // ✅ Always save as a new attempt (no overwriting)
        Result result = new Result();
        result.setScore(totalScore);
        result.setUser(user);
        result.setAttemptNumber(nextAttempt);
        resultRepo.save(result);

        return "Score: " + totalScore;
    }

    // ================= LEADERBOARD =================
    // ================= LEADERBOARD =================
    @GetMapping("/leaderboard")
    public List<Map<String, Object>> getLeaderboard(){

        // Group by username, keep only the best score per user
        Map<String, Integer> bestScores = new LinkedHashMap<>();

        resultRepo.findAllByOrderByScoreDesc()
                .stream()
                .filter(r -> r.getUser() != null)
                .forEach(r -> {
                    String username = r.getUser().getUsername();
                    // Only add if not already present (first = highest due to ordering)
                    bestScores.putIfAbsent(username, r.getScore());
                });

        List<Map<String, Object>> leaderboard = new java.util.ArrayList<>();
        bestScores.forEach((username, score) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("username", username);
            map.put("score", score);
            leaderboard.add(map);
        });

        return leaderboard;
    }

    // ================= SCORE HISTORY =================
    @GetMapping("/score-history")
    public List<Map<String, Object>> getScoreHistory(@RequestParam String username){

        User user = userRepo.findByUsername(username);

        if(user == null) return List.of();

        return resultRepo.findByUserOrderByAttemptNumberAsc(user)
                .stream()
                .map(r -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("attempt", r.getAttemptNumber());
                    map.put("score", r.getScore());
                    return map;
                })
                .toList();
    }
}