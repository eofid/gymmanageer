package com.gym.management.gymmanager.controller;

import com.gym.management.gymmanager.service.VisitCounterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/visit")
public class VisitController {
    private final VisitCounterService visitCounterService;

    public VisitController(VisitCounterService visitCounterService) {
        this.visitCounterService = visitCounterService;
    }

    @GetMapping("/track")
    public String trackVisit() {
        visitCounterService.increment();
        return "Visit tracked";
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getVisitCount() {
        return ResponseEntity.ok(visitCounterService.getCount()); }
    }
