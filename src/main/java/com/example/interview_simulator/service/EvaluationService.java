package com.example.interview_simulator.service;



import org.springframework.stereotype.Service;

@Service
public class EvaluationService {

    public int evaluateAnswer(String answer, String keywords) {

        int score = 0;
        String[] keyList = keywords.split(",");

        for(String key : keyList) {
            if(answer.toLowerCase().contains(key.trim().toLowerCase())) {
                score++;
            }
        }

        return score;
    }
}
