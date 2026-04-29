package com.example.interview_simulator.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import com.example.interview_simulator.entity.*;
import com.example.interview_simulator.repository.*;
import com.example.interview_simulator.service.*;

@CrossOrigin
@RestController
public class InterviewController {

    @Autowired
    private QuestionRepository questionRepo;

    @Autowired
    private ResultRepository resultRepo;

    @Autowired
    private EvaluationService evaluationService;

    @GetMapping("/questions")
    public List<Question> getQuestions() {
        return questionRepo.findAll();
    }

    @PostMapping("/submit-answers")
    public String submitAnswers(@RequestBody Map<String, Object> data) {

        String username = (String) data.get("username");
        List<Map<String, Object>> answers = (List<Map<String, Object>>) data.get("answers");

        int totalScore = 0;

        for(Map<String, Object> ans : answers) {

            int qid = (int) ans.get("qid");
            String userAns = (String) ans.get("answer");

            Optional<Question> optionalQ = questionRepo.findById(qid);

            if(optionalQ.isPresent()) {
                Question q = optionalQ.get();

                if(q.getKeywords() != null) {
                    totalScore += evaluationService.evaluateAnswer(userAns, q.getKeywords());
                }
            }
        }

        Result result = new Result();
        result.setUsername(username);
        result.setScore(totalScore);

        resultRepo.save(result);

        return "Score: " + totalScore;
    }
}
