package com.gym.management.gymmanager;

import com.gym.management.gymmanager.model.Trainer;
import com.gym.management.gymmanager.repository.TrainerRepository;
import com.gym.management.gymmanager.service.TrainerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private TrainerService trainerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveTrainer_ShouldReturnSavedTrainer() {
        Trainer trainer = new Trainer();
        trainer.setName("Ivan");
        trainer.setTrainingType("Strength");
        trainer.setGender("Male");

        when(trainerRepository.save(trainer)).thenReturn(trainer);

        Trainer saved = trainerService.saveTrainer(trainer);
        assertEquals("Ivan", saved.getName());
        verify(trainerRepository).save(trainer);
    }

    @Test
    void getTrainerById_ShouldReturnTrainer() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setName("Anna");

        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        Trainer result = trainerService.getTrainerById(1L);
        assertNotNull(result);
        assertEquals("Anna", result.getName());
    }

    @Test
    void getAllTrainers_ShouldReturnListOfTrainers() {
        Trainer t1 = new Trainer();
        t1.setName("John");
        Trainer t2 = new Trainer();
        t2.setName("Lena");

        when(trainerRepository.findAll()).thenReturn(Arrays.asList(t1, t2));

        List<Trainer> list = trainerService.getAllTrainers();
        assertEquals(2, list.size());
    }

    @Test
    void getTrainerById_ShouldReturnNull_IfNotFound() {
        when(trainerRepository.findById(999L)).thenReturn(Optional.empty());

        Trainer result = trainerService.getTrainerById(999L);

        assertNull(result);
    }

    @Test
    void updateTrainer_ShouldReturnNull_IfTrainerNotFound() {
        Trainer updated = new Trainer();
        updated.setName("Ghost");
        updated.setTrainingType("Unknown");
        updated.setGender("N/A");

        when(trainerRepository.findById(404L)).thenReturn(Optional.empty());

        Trainer result = trainerService.updateTrainer(404L, updated);

        assertNull(result);
    }


    @Test
    void updateTrainer_ShouldReturnUpdatedTrainer() {
        Trainer existing = new Trainer();
        existing.setId(1L);
        existing.setName("Old");
        existing.setTrainingType("Yoga");
        existing.setGender("Female");

        Trainer updated = new Trainer();
        updated.setName("New");
        updated.setTrainingType("Boxing");
        updated.setGender("Male");

        when(trainerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(trainerRepository.save(existing)).thenReturn(existing);

        Trainer result = trainerService.updateTrainer(1L, updated);

        assertEquals("New", result.getName());
        assertEquals("Boxing", result.getTrainingType());
        assertEquals("Male", result.getGender());
    }

    @Test
    void deleteTrainer_ShouldReturnTrue_IfExists() {
        when(trainerRepository.existsById(1L)).thenReturn(true);

        boolean deleted = trainerService.deleteTrainer(1L);
        assertTrue(deleted);
        verify(trainerRepository).deleteById(1L);
    }

    @Test
    void deleteTrainer_ShouldReturnFalse_IfNotExists() {
        when(trainerRepository.existsById(2L)).thenReturn(false);

        boolean deleted = trainerService.deleteTrainer(2L);
        assertFalse(deleted);
    }
}
