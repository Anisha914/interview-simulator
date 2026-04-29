package com.example.interview_simulator.controller;

import java.util.List;

public class SubmissionRequest {

    private String username;
    private List<Answer> answers;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }
}