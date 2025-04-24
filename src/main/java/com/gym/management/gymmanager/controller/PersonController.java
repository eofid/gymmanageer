package com.gym.management.gymmanager.controller;

import com.gym.management.gymmanager.cache.PersonCache;
import com.gym.management.gymmanager.model.Person;
import com.gym.management.gymmanager.service.PersonService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/persons")
@Tag(name = "Персоны", description = "Управление клиентами спортзала")
public class PersonController {

    @Autowired
    private PersonService personService;

    @Autowired
    private PersonCache personCache;

    @PostMapping
    @Operation(summary = "Создать нового клиента")
    public ResponseEntity<Person> addPerson(@RequestBody Person person) {
        Person savedPerson = personService.savePerson(person);
        return ResponseEntity.ok(savedPerson);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить клиента по ID")
    public ResponseEntity<Person> getPersonById(@PathVariable Long id) {
        Person person = personService.getPersonById(id);
        if (person == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(person);
    }

    @GetMapping
    @Operation(summary = "Получить всех клиентов")
    public ResponseEntity<List<Person>> getAllPeople() {
        return ResponseEntity.ok(personService.getAllPeople());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить клиента по ID")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        boolean deleted = personService.deletePerson(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить данные клиента")
    public ResponseEntity<Person> updatePerson(@PathVariable Long id, @RequestBody Person personDetails) {
        Person updatedPerson = personService.updatePerson(id, personDetails);
        if (updatedPerson != null) {
            return ResponseEntity.ok(updatedPerson);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{personId}/trainer/{trainerId}")
    @Operation(summary = "Назначить тренера клиенту")
    public ResponseEntity<Person> assignTrainer(@PathVariable Long personId, @PathVariable Long trainerId) {
        Person updated = personService.assignTrainerToPerson(personId, trainerId);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{personId}/gym/{gymId}")
    @Operation(summary = "Назначить спортзал клиенту")
    public ResponseEntity<Person> assignGym(@PathVariable Long personId, @PathVariable Long gymId) {
        Person updated = personService.assignGymToPerson(personId, gymId);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/by-gym-type")
    @Operation(summary = "Получить клиентов по типу спортзала (JPQL)")
    public ResponseEntity<List<Person>> getPeopleByGymType(@RequestParam String gymType) {
        List<Person> people = personService.getPersonsByGymType(gymType);
        return ResponseEntity.ok(people);
    }

    @GetMapping("/by-gym-type-native")
    @Operation(summary = "Получить клиентов по типу спортзала (native SQL)")
    public ResponseEntity<List<Person>> getPeopleByGymTypeNative(@RequestParam String gymType) {
        List<Person> people = personService.getPersonsByGymTypeNative(gymType);
        return ResponseEntity.ok(people);
    }

    @GetMapping("/cache")
    @Operation(summary = "Получить содержимое кеша клиентов")
    public ResponseEntity<Map<Long, Person>> getPersonCache() {
        return ResponseEntity.ok(personCache.getAll());
    }

    @DeleteMapping("/cache/clear")
    @Operation(summary = "Очистить кеш клиентов")
    public ResponseEntity<String> clearCache() {
        personCache.clear();
        return ResponseEntity.ok("Person cache cleared");
    }
}
