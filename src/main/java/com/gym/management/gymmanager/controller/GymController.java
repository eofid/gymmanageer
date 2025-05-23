package com.gym.management.gymmanager.controller;

import com.gym.management.gymmanager.model.Gym;
import com.gym.management.gymmanager.service.GymService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/gyms")
public class GymController {

    private GymService gymService;

    // 1. Добавить новый зал
    @PostMapping
    public ResponseEntity<Gym> addGym(@RequestBody Gym gym) {
        Gym savedGym = gymService.saveGym(gym);
        return ResponseEntity.ok(savedGym);
    }

    // 2. Получить все залы
    @GetMapping
    public ResponseEntity<List<Gym>> getAllGyms() {
        List<Gym> gyms = gymService.getAllGyms();
        return ResponseEntity.ok(gyms);
    }

    // 3. Получить один зал по ID
    @GetMapping("/{id}")
    public ResponseEntity<Gym> getGymById(@PathVariable Long id) {
        Gym gym = gymService.getGymById(id);
        return gym != null ? ResponseEntity.ok(gym) : ResponseEntity.notFound().build();
    }

    // 4. Обновить зал по ID
    @PutMapping("/{id}")
    public ResponseEntity<Gym> updateGym(@PathVariable Long id, @RequestBody Gym gymDetails) {
        Gym updatedGym = gymService.updateGym(id, gymDetails);
        return updatedGym != null ? ResponseEntity.ok(updatedGym) : ResponseEntity.notFound().build();
    }

    // 5. Удалить зал по ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGym(@PathVariable Long id) {
        boolean deleted = gymService.deleteGym(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
