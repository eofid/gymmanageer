package com.gym.management.gymmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.management.gymmanager.controller.GymController;
import com.gym.management.gymmanager.model.Gym;
import com.gym.management.gymmanager.service.GymService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class GymControllerTest {

    @InjectMocks
    private GymController gymController;

    @Mock
    private GymService gymService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(gymController).build();
    }

    @Test
    void testAddGym() throws Exception {
        Gym gym = new Gym();
        gym.setId(1L);
        gym.setType("Standard");

        when(gymService.saveGym(any(Gym.class))).thenReturn(gym);

        mockMvc.perform(post("/api/gyms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"Standard\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.type").value("Standard"));

        verify(gymService, times(1)).saveGym(any(Gym.class));
    }

    @Test
    void testGetAllGyms() throws Exception {
        mockMvc.perform(get("/api/gyms"))
                .andExpect(status().isOk());

        verify(gymService, times(1)).getAllGyms();
    }

    @Test
    void testGetGymById() throws Exception {
        Gym gym = new Gym();
        gym.setId(1L);
        gym.setType("VIP");

        when(gymService.getGymById(1L)).thenReturn(gym);

        mockMvc.perform(get("/api/gyms/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.type").value("VIP"));

        verify(gymService, times(1)).getGymById(1L);
    }

    @Test
    void testGetGymById_NotFound() throws Exception {
        when(gymService.getGymById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/gyms/999"))
                .andExpect(status().isNotFound());

        verify(gymService, times(1)).getGymById(999L);
    }

    @Test
    void testUpdateGym_WhenFound() throws Exception {
        Gym updatedGym = new Gym();
        updatedGym.setId(1L);
        updatedGym.setType("Updated");

        when(gymService.updateGym(eq(1L), any(Gym.class))).thenReturn(updatedGym);

        mockMvc.perform(put("/api/gyms/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedGym)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("Updated"));
    }

    @Test
    void testUpdateGym_WhenNotFound() throws Exception {
        Gym updatedGym = new Gym();
        updatedGym.setType("Nonexistent");

        when(gymService.updateGym(eq(1L), any(Gym.class))).thenReturn(null);

        mockMvc.perform(put("/api/gyms/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedGym)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteGym() throws Exception {
        when(gymService.deleteGym(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/gyms/1"))
                .andExpect(status().isNoContent());

        verify(gymService, times(1)).deleteGym(1L);
    }

    @Test
    void testDeleteGym_NotFound() throws Exception {
        when(gymService.deleteGym(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/gyms/999"))
                .andExpect(status().isNotFound());

        verify(gymService, times(1)).deleteGym(999L);
    }
}
