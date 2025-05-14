package com.gym.management.gymmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.management.gymmanager.controller.TrainerController;
import com.gym.management.gymmanager.model.Trainer;
import com.gym.management.gymmanager.service.TrainerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TrainerControllerTest {

    @InjectMocks
    private TrainerController trainerController;

    @Mock
    private TrainerService trainerService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(trainerController).build();
    }

    @Test
    void testAddTrainer() throws Exception {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setName("Test Trainer");

        when(trainerService.saveTrainer(any(Trainer.class))).thenReturn(trainer);

        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));

        verify(trainerService, times(1)).saveTrainer(any(Trainer.class));
    }

    @Test
    void testGetAllTrainers() throws Exception {
        when(trainerService.getAllTrainers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/trainers"))
                .andExpect(status().isOk());

        verify(trainerService, times(1)).getAllTrainers();
    }

    @Test
    void testGetTrainerById_WhenFound() throws Exception {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setName("Found");

        when(trainerService.getTrainerById(1L)).thenReturn(trainer);

        mockMvc.perform(get("/api/trainers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Found"));
    }

    @Test
    void testGetTrainerById_WhenNotFound() throws Exception {
        when(trainerService.getTrainerById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/trainers/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateTrainer_WhenFound() throws Exception {
        Trainer updatedTrainer = new Trainer();
        updatedTrainer.setId(1L);
        updatedTrainer.setName("Updated");

        when(trainerService.updateTrainer(eq(1L), any(Trainer.class))).thenReturn(updatedTrainer);

        mockMvc.perform(put("/api/trainers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTrainer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void testUpdateTrainer_WhenNotFound() throws Exception {
        Trainer updatedTrainer = new Trainer();
        updatedTrainer.setName("NonExistent");

        when(trainerService.updateTrainer(eq(1L), any(Trainer.class))).thenReturn(null);

        mockMvc.perform(put("/api/trainers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTrainer)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteTrainer_WhenExists() throws Exception {
        when(trainerService.deleteTrainer(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/trainers/1"))
                .andExpect(status().isNoContent());

        verify(trainerService, times(1)).deleteTrainer(1L);
    }

    @Test
    void testDeleteTrainer_WhenNotExists() throws Exception {
        when(trainerService.deleteTrainer(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/trainers/999"))
                .andExpect(status().isNotFound());
    }
}
