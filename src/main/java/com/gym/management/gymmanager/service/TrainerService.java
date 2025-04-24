package com.gym.management.gymmanager.service;

import com.gym.management.gymmanager.model.Trainer;
import com.gym.management.gymmanager.repository.TrainerRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainerService {
    @Autowired
    private TrainerRepository trainerRepository;

    // Сохранение нового тренера
    public Trainer saveTrainer(Trainer trainer) {
        return trainerRepository.save(trainer);
    }

    // Получение тренера по ID
    public Trainer getTrainerById(Long id) {
        return trainerRepository.findById(id).orElse(null);
    }

    // Получение всех тренеров
    public List<Trainer> getAllTrainers() {
        return trainerRepository.findAll();
    }

    // Обновление тренера по ID
    public Trainer updateTrainer(Long id, Trainer updatedTrainer) {
        Trainer existingTrainer = trainerRepository.findById(id).orElse(null);
        if (existingTrainer != null) {
            // Обновляем информацию тренера
            existingTrainer.setName(updatedTrainer.getName());
            existingTrainer.setTrainingType(updatedTrainer.getTrainingType());
            existingTrainer.setGender(updatedTrainer.getGender());
            // Сохраняем обновленного тренера
            return trainerRepository.save(existingTrainer);
        }
        return null;  // Возвращаем null, если тренер с таким ID не найден
    }

    // Удаление тренера по ID
    public boolean deleteTrainer(Long id) {
        if (trainerRepository.existsById(id)) {
            trainerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}