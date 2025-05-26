package com.gym.management.gymmanager.service;

import com.gym.management.gymmanager.model.Trainer;
import com.gym.management.gymmanager.repository.TrainerRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TrainerService {
    private TrainerRepository trainerRepository;

    // 1. Сохранение нового тренера
    public Trainer saveTrainer(Trainer trainer) {
        return trainerRepository.save(trainer);
    }

    // 2. Получение тренера по ID
    public Trainer getTrainerById(Long id) {
        return trainerRepository.findById(id).orElse(null);
    }

    // 3. Получение всех тренеров
    public List<Trainer> getAllTrainers() {
        return trainerRepository.findAll();
    }

    // 4. Обновление тренера по ID
    public Trainer updateTrainer(Long id, Trainer updatedTrainer) {
        Trainer existingTrainer = trainerRepository.findById(id).orElse(null);
        if (existingTrainer != null) {
            existingTrainer.setName(updatedTrainer.getName());
            existingTrainer.setTrainingType(updatedTrainer.getTrainingType());
            existingTrainer.setGender(updatedTrainer.getGender());
            return trainerRepository.save(existingTrainer);
        }
        return null;
    }

    // 5. Удаление тренера по ID
    public boolean deleteTrainer(Long id) {
        if (trainerRepository.existsById(id)) {
            trainerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
