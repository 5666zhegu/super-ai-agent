package com.geek.superaiagent.controller;

import jakarta.validation.constraints.FutureOrPresent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {
    @GetMapping
    public String health() {
        return "ok";
    }
}
