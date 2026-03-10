package com.example.depu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/hall")
    public String hall() {
        return "hall";
    }

    @GetMapping("/game")
    public String game() {
        return "game";
    }

    @GetMapping("/leaderboard")
    public String leaderboard() {
        return "leaderboard";
    }

    @GetMapping("/settings")
    public String settings() {
        return "settings";
    }
}
