package com.gym.management.gymmanager.service;

import com.gym.management.gymmanager.model.Gym;
import com.gym.management.gymmanager.repository.GymRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GymService {
    @Autowired
    private GymRepository gymRepository;

    // 1. Сохранение зала
    public Gym saveGym(Gym gym) {
        return gymRepository.save(gym);
    }

    // 2. Получение всех залов
    public List<Gym> getAllGyms() {
        return gymRepository.findAll();
    }

    // 3. Получение зала по ID
    public Gym getGymById(Long id) {
        return gymRepository.findById(id).orElse(null);
    }

    // 4. Обновление зала
    public Gym updateGym(Long id, Gym gymDetails) {
        Optional<Gym> optionalGym = gymRepository.findById(id);
        if (optionalGym.isPresent()) {
            Gym gym = optionalGym.get();
            gym.setType(gymDetails.getType());
            gym.setNumber(gymDetails.getNumber());
            gym.setAddress(gymDetails.getAddress());
            return gymRepository.save(gym);
        }
        return null;
    }

    // 5. Удаление зала
    public boolean deleteGym(Long id) {
        if (gymRepository.existsById(id)) {
            gymRepository.deleteById(id);
            return true;
        }
        return false;
    }
}