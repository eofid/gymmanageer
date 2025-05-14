package com.gym.management.gymmanager;

import com.gym.management.gymmanager.cache.PersonCache;
import com.gym.management.gymmanager.exception.ResourceNotFoundException;
import com.gym.management.gymmanager.exception.ValidationException;
import com.gym.management.gymmanager.model.Gym;
import com.gym.management.gymmanager.model.Person;
import com.gym.management.gymmanager.model.Trainer;
import com.gym.management.gymmanager.repository.GymRepository;
import com.gym.management.gymmanager.repository.PersonRepository;
import com.gym.management.gymmanager.repository.TrainerRepository;
import com.gym.management.gymmanager.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    @InjectMocks
    private PersonService personService;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private GymRepository gymRepository;

    @Mock
    private PersonCache personCache;

    private final Long personId = 54L;

    @BeforeEach
    void setUp() {
        reset(personRepository, trainerRepository, gymRepository, personCache);
    }

    @Test
    void testSavePerson_Success() {
        Person person = new Person();
        person.setName("Test Client");
        when(personRepository.save(any(Person.class))).thenReturn(person);
        Person savedPerson = personService.savePerson(person);
        assertNotNull(savedPerson);
        verify(personRepository, times(1)).save(person);
        verify(personCache, times(1)).putToIdCache(savedPerson.getId(), savedPerson);
    }

    @Test
    void testSavePerson_ValidationException() {
        Person person = new Person();
        person.setName("  ");
        assertThrows(ValidationException.class, () -> personService.savePerson(person));
        verify(personRepository, never()).save(any());
        verify(personCache, never()).putToIdCache(any(), any());
    }

    @Test
    void testGetPersonById_FromCache() {
        Person cachedPerson = new Person();
        cachedPerson.setId(personId);
        cachedPerson.setName("Cached Client");
        when(personCache.getPersonByIdCache(personId)).thenReturn(cachedPerson);
        Person result = personService.getPersonById(personId);
        assertNotNull(result);
        assertEquals(cachedPerson, result);
        verify(personCache, times(1)).getPersonByIdCache(personId);
        verify(personRepository, never()).findById(any());
    }

    @Test
    void testGetPersonById_FromRepository() {
        Person dbPerson = new Person();
        dbPerson.setId(personId);
        dbPerson.setName("DB Client");
        when(personCache.getPersonByIdCache(personId)).thenReturn(null);
        when(personRepository.findById(personId)).thenReturn(Optional.of(dbPerson));
        Person result = personService.getPersonById(personId);
        assertNotNull(result);
        assertEquals(dbPerson, result);
        verify(personRepository, times(1)).findById(personId);
        verify(personCache, times(1)).putToIdCache(personId, dbPerson);
    }

    @Test
    void testSavePeople_Success() {
        Person person1 = new Person();
        person1.setName("Person 1");
        Person person2 = new Person();
        person2.setName("Person 2");
        List<Person> people = Arrays.asList(person1, person2);
        when(personRepository.saveAll(people)).thenReturn(people);
        List<Person> savedPeople = personService.savePeople(people);
        assertEquals(2, savedPeople.size());
        verify(personRepository, times(1)).saveAll(people);
        verify(personCache, times(2)).putToIdCache(any(), any());
    }

    @Test
    void testSavePeople_ValidationException_NullList() {
        assertThrows(ValidationException.class, () -> personService.savePeople(null));
    }

    @Test
    void testSavePeople_ValidationException_InvalidPerson() {
        Person invalidPerson = new Person();
        assertThrows(ValidationException.class, () -> personService.savePeople(Collections.singletonList(invalidPerson)));
    }

    @Test
    void testGetAllPeople() {
        Person person = new Person();
        person.setId(1L);
        person.setName("Test Person");

        when(personRepository.findAll()).thenReturn(List.of(person));

        List<Person> result = personService.getAllPeople();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Person", result.get(0).getName());

        verify(personRepository, times(1)).findAll();
    }


    @Test
    void testUpdatePerson_Success() {
        Person existingPerson = new Person();
        existingPerson.setId(personId);
        existingPerson.setName("Old Name");

        Person updatedDetails = new Person();
        updatedDetails.setName("New Name");
        updatedDetails.setPhoneNumber("123456789");

        when(personRepository.findById(personId)).thenReturn(Optional.of(existingPerson));
        when(personRepository.save(existingPerson)).thenReturn(existingPerson);

        Person updatedPerson = personService.updatePerson(personId, updatedDetails);

        assertNotNull(updatedPerson);
        assertEquals("New Name", updatedPerson.getName());
        assertEquals("123456789", updatedPerson.getPhoneNumber());

        verify(personRepository, times(1)).findById(personId);
        verify(personRepository, times(1)).save(existingPerson);
        verify(personCache, times(1)).putToIdCache(personId, existingPerson);
    }

    @Test
    void testUpdatePerson_ValidationException() {
        Person existingPerson = new Person();
        existingPerson.setId(personId);
        existingPerson.setName("Old Name");

        Person updatedDetails = new Person();
        updatedDetails.setName("");

        when(personRepository.findById(personId)).thenReturn(Optional.of(existingPerson));

        assertThrows(ValidationException.class, () -> personService.updatePerson(personId, updatedDetails));
        verify(personRepository, never()).save(any());
    }

    @Test
    void testAssignTrainerToPerson() {
        Person person = new Person();
        person.setId(personId);
        person.setName("Test Client");

        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setName("Test Trainer");

        when(personRepository.findById(personId)).thenReturn(Optional.of(person));
        when(trainerRepository.findById(trainer.getId())).thenReturn(Optional.of(trainer));
        when(personRepository.save(any(Person.class))).thenReturn(person);

        Person updatedPerson = personService.assignTrainerToPerson(personId, trainer.getId());

        assertNotNull(updatedPerson);
        assertEquals(trainer, updatedPerson.getTrainer());

        verify(personRepository, times(1)).findById(personId);
        verify(trainerRepository, times(1)).findById(trainer.getId());
        verify(personRepository, times(1)).save(person);
        verify(personCache, times(1)).putToIdCache(personId, person);
    }

    @Test
    void testAssignGymToPerson() {
        Person person = new Person();
        person.setId(personId);
        person.setName("Test Client");

        Gym gym = new Gym();
        gym.setId(1L);
        gym.setType("VIP");

        when(personRepository.findById(personId)).thenReturn(Optional.of(person));
        when(gymRepository.findById(gym.getId())).thenReturn(Optional.of(gym));
        when(personRepository.save(any(Person.class))).thenReturn(person);

        Person updatedPerson = personService.assignGymToPerson(personId, gym.getId());

        assertNotNull(updatedPerson);
        assertEquals(gym, updatedPerson.getGym());

        verify(personRepository, times(1)).findById(personId);
        verify(gymRepository, times(1)).findById(gym.getId());
        verify(personRepository, times(1)).save(person);
        verify(personCache, times(1)).putToIdCache(personId, person);
    }

    @Test
    void testDeletePerson() {
        Person person = new Person();
        person.setId(personId);
        person.setName("Test Client");

        when(personCache.getPersonByIdCache(personId)).thenReturn(person);
        when(personRepository.existsById(personId)).thenReturn(true);
        doNothing().when(personRepository).deleteById(personId);

        boolean deleted = personService.deletePerson(personId);

        assertTrue(deleted);

        verify(personCache, times(1)).getPersonByIdCache(personId);
        verify(personCache, times(1)).removeFromIdCache(personId);
        verify(personRepository, times(1)).existsById(personId);
        verify(personRepository, times(1)).deleteById(personId);
    }

    @Test
    void testDeletePerson_NotFound() {
        when(personCache.getPersonByIdCache(personId)).thenReturn(null);
        when(personRepository.existsById(personId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> personService.deletePerson(personId));
        verify(personRepository, times(1)).existsById(personId);
        verify(personRepository, never()).deleteById(personId);
    }
}
