package com.gym.management.gymmanager;

import com.gym.management.gymmanager.model.Gym;
import com.gym.management.gymmanager.repository.GymRepository;
import com.gym.management.gymmanager.service.GymService;
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

public class GymServiceTest {

    @Mock
    private GymRepository gymRepository;

    @InjectMocks
    private GymService gymService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveGym_ShouldReturnSavedGym() {
        Gym gym = new Gym("Fitness", "123 Street", "A1");
        when(gymRepository.save(gym)).thenReturn(gym);

        Gym saved = gymService.saveGym(gym);
        assertEquals(gym, saved);
        verify(gymRepository).save(gym);
    }

    @Test
    void getAllGyms_ShouldReturnAllGyms() {
        Gym gym1 = new Gym("Fitness", "123 Street", "A1");
        Gym gym2 = new Gym("Yoga", "456 Avenue", "B2");
        when(gymRepository.findAll()).thenReturn(Arrays.asList(gym1, gym2));

        List<Gym> gyms = gymService.getAllGyms();
        assertEquals(2, gyms.size());
    }

    @Test
    void getGymById_ShouldReturnGym() {
        Gym gym = new Gym("Fitness", "123 Street", "A1");
        gym.setId(1L);
        when(gymRepository.findById(1L)).thenReturn(Optional.of(gym));

        Gym result = gymService.getGymById(1L);
        assertNotNull(result);
        assertEquals("Fitness", result.getType());
    }

    @Test
    void getGymById_ShouldReturnNull_IfNotFound() {
        when(gymRepository.findById(99L)).thenReturn(Optional.empty());
        Gym result = gymService.getGymById(99L);
        assertNull(result);
    }

    @Test
    void updateGym_ShouldReturnNull_IfGymDoesNotExist() {
        Gym updated = new Gym("Updated", "Addr", "N2");
        when(gymRepository.findById(999L)).thenReturn(Optional.empty());
        Gym result = gymService.updateGym(999L, updated);
        assertNull(result);
    }


    @Test
    void updateGym_ShouldReturnUpdatedGym() {
        Gym existing = new Gym("Old", "Old Addr", "O1");
        existing.setId(1L);

        Gym updated = new Gym("New", "New Addr", "N1");

        when(gymRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(gymRepository.save(existing)).thenReturn(existing);

        Gym result = gymService.updateGym(1L, updated);

        assertEquals("New", result.getType());
        assertEquals("New Addr", result.getAddress());
        assertEquals("N1", result.getNumber());
    }

    @Test
    void deleteGym_ShouldReturnTrue_IfExists() {
        when(gymRepository.existsById(1L)).thenReturn(true);
        boolean deleted = gymService.deleteGym(1L);
        assertTrue(deleted);
        verify(gymRepository).deleteById(1L);
    }

    @Test
    void deleteGym_ShouldReturnFalse_IfNotExists() {
        when(gymRepository.existsById(2L)).thenReturn(false);
        boolean deleted = gymService.deleteGym(2L);
        assertFalse(deleted);
    }
}
