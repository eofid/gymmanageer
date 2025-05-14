package com.gym.management.gymmanager.controller;

import com.gym.management.gymmanager.cache.PersonCache;
import com.gym.management.gymmanager.model.Person;
import com.gym.management.gymmanager.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Person> addPerson(@Valid @RequestBody Person person) {
        return ResponseEntity.ok(personService.savePerson(person));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Person>> savePeopleBulk(@RequestBody List<Person> people) {
        List<Person> savedPeople = personService.savePeople(people);
        return ResponseEntity.ok(savedPeople);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить клиента по ID")
    public ResponseEntity<Person> getPersonById(@PathVariable Long id) {
        return ResponseEntity.ok(personService.getPersonById(id));
    }

    @GetMapping
    @Operation(summary = "Получить всех клиентов")
    public ResponseEntity<List<Person>> getAllPeople() {
        return ResponseEntity.ok(personService.getAllPeople());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить клиента по ID")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        personService.deletePerson(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить данные клиента")
    public ResponseEntity<Person> updatePerson(@PathVariable Long id, @RequestBody Person personDetails) {
        return ResponseEntity.ok(personService.updatePerson(id, personDetails));
    }

    @PutMapping("/{personId}/trainer/{trainerId}")
    @Operation(summary = "Назначить тренера клиенту")
    public ResponseEntity<Person> assignTrainer(@PathVariable Long personId, @PathVariable Long trainerId) {
        return ResponseEntity.ok(personService.assignTrainerToPerson(personId, trainerId));
    }

    @PutMapping("/{personId}/gym/{gymId}")
    @Operation(summary = "Назначить спортзал клиенту")
    public ResponseEntity<Person> assignGym(@PathVariable Long personId, @PathVariable Long gymId) {
        return ResponseEntity.ok(personService.assignGymToPerson(personId, gymId));
    }

    @GetMapping("/by-gym-type")
    @Operation(summary = "Получить клиентов по типу спортзала (JPQL)")
    public ResponseEntity<List<Person>> getPeopleByGymType(@RequestParam String gymType) {
        return ResponseEntity.ok(personService.getPersonsByGymType(gymType));
    }

    @GetMapping("/by-gym-type-native")
    @Operation(summary = "Получить клиентов по типу спортзала (native SQL)")
    public ResponseEntity<List<Person>> getPeopleByGymTypeNative(@RequestParam String gymType) {
        return ResponseEntity.ok(personService.getPersonsByGymTypeNative(gymType));
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
