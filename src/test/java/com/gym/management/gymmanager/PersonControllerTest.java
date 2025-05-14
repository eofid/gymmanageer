package com.gym.management.gymmanager;

import com.gym.management.gymmanager.controller.PersonController;
import com.gym.management.gymmanager.model.Person;
import com.gym.management.gymmanager.service.PersonService;
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
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class PersonControllerTest {

    @InjectMocks
    private PersonController personController;

    @Mock
    private PersonService personService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(personController).build();
    }

    @Test
    void testAddPerson() throws Exception {
        Person person = new Person();
        person.setId(1L);
        person.setName("Test Client");

        when(personService.savePerson(any(Person.class))).thenReturn(person);

        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test Client\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Client"));

        verify(personService, times(1)).savePerson(any(Person.class));
    }

    @Test
    void testGetPersonById() throws Exception {
        Person person = new Person();
        person.setId(1L);
        person.setName("Test Client");

        when(personService.getPersonById(1L)).thenReturn(person);

        mockMvc.perform(get("/api/persons/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Client"));

        verify(personService, times(1)).getPersonById(1L);
    }

    @Test
    void testDeletePerson() throws Exception {
        when(personService.deletePerson(anyLong())).thenReturn(true);

        mockMvc.perform(delete("/api/persons/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(personService, times(1)).deletePerson(1L);
    }

    @Test
    void testUpdatePerson() throws Exception {
        Person updatedPerson = new Person();
        updatedPerson.setId(1L);
        updatedPerson.setName("Updated Name");

        when(personService.updatePerson(eq(1L), any(Person.class))).thenReturn(updatedPerson);

        mockMvc.perform(put("/api/persons/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Name\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Name"));

        verify(personService, times(1)).updatePerson(eq(1L), any(Person.class));
    }

    @Test
    void testGetPeopleByGymType() throws Exception {
        Person person = new Person();
        person.setId(1L);
        person.setName("Test Client");
        List<Person> people = Collections.singletonList(person);
        when(personService.getPersonsByGymType("Fitness")).thenReturn(people);

        mockMvc.perform(get("/api/persons/by-gym-type")
                        .param("gymType", "Fitness"))
                .andExpect(status().isOk())  // Ожидаем статус 200 OK
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Client"));
        verify(personService, times(1)).getPersonsByGymType("Fitness");
    }





    @Test
    void testGetAllPeople() throws Exception {
        List<Person> people = Collections.singletonList(new Person());
        when(personService.getAllPeople()).thenReturn(people);

        mockMvc.perform(get("/api/persons"))
                .andExpect(status().isOk());

        verify(personService, times(1)).getAllPeople();
    }
}
