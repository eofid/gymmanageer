package com.gym.management.gymmanager.controller;

import com.gym.management.gymmanager.model.Trainer;
import com.gym.management.gymmanager.service.TrainerService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trainers")
public class TrainerController {
    private TrainerService trainerService;

    // 1. Создание нового тренера
    @PostMapping
    public ResponseEntity<Trainer> addTrainer(@RequestBody Trainer trainer) {
        Trainer savedTrainer = trainerService.saveTrainer(trainer);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTrainer);
    }

    // 2. Получение всех тренеров
    @GetMapping
    public ResponseEntity<List<Trainer>> getAllTrainers() {
        List<Trainer> trainers = trainerService.getAllTrainers();
        return ResponseEntity.ok(trainers);
    }

    // 3. Получение тренера по ID
    @GetMapping("/{id}")
    public ResponseEntity<Trainer> getTrainerById(@PathVariable Long id) {
        Trainer trainer = trainerService.getTrainerById(id);
        if (trainer != null) {
            return ResponseEntity.ok(trainer);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // 404 если тренер не найден
        }
    }

    // 4. Обновление тренера
    @PutMapping("/{id}")
    public ResponseEntity<Trainer> updateTrainer(@PathVariable Long id, @RequestBody Trainer trainer) {
        Trainer updatedTrainer = trainerService.updateTrainer(id, trainer);
        if (updatedTrainer != null) {
            return ResponseEntity.ok(updatedTrainer);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // 404 если тренер не найден
        }
    }

    // 5. Удаление тренера
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrainer(@PathVariable Long id) {
        boolean deleted = trainerService.deleteTrainer(id);
        if (deleted) {
            return ResponseEntity.noContent().build();  // 204 No Content если тренер удален
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // 404 если тренер не найден
        }
    }
}
