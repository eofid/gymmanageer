package com.gym.management.gymmanager.controller;

import com.gym.management.gymmanager.service.VisitCounterService;
import org.springframework.web.bind.annotation.*;

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
    public int getVisitCount() {
        return visitCounterService.getCount();
    }
}
