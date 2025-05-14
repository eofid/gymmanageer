package com.gym.management.gymmanager.service;

import com.gym.management.gymmanager.cache.PersonCache;
import com.gym.management.gymmanager.exception.ResourceNotFoundException;
import com.gym.management.gymmanager.exception.ValidationException;
import com.gym.management.gymmanager.model.Gym;
import com.gym.management.gymmanager.model.Person;
import com.gym.management.gymmanager.model.Trainer;
import com.gym.management.gymmanager.repository.GymRepository;
import com.gym.management.gymmanager.repository.PersonRepository;
import com.gym.management.gymmanager.repository.TrainerRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PersonService {
    private final PersonRepository personRepository;
    private final PersonCache personCache;
    private final TrainerRepository trainerRepository;
    private final GymRepository gymRepository;

    public PersonService(PersonRepository personRepository,
                         PersonCache personCache,
                         TrainerRepository trainerRepository,
                         GymRepository gymRepository) {
        this.personRepository = personRepository;
        this.personCache = personCache;
        this.trainerRepository = trainerRepository;
        this.gymRepository = gymRepository;
    }

    public Person savePerson(Person person) {
        if (person.getName() == null || person.getName().trim().isEmpty()) {
            throw new ValidationException("Имя клиента обязательно");
        }

        Person saved = personRepository.save(person);
        personCache.putToIdCache(saved.getId(), saved);
        return saved;
    }

    public Person getPersonById(Long id) {
        Person cachedPerson = personCache.getPersonByIdCache(id);
        if (cachedPerson != null) {
            return cachedPerson;
        }

        return personRepository.findById(id)
                .map(person -> {
                    personCache.putToIdCache(id, person);
                    return person;
                })
                .orElseThrow(() -> new ResourceNotFoundException("Клиент с ID " + id + " не найден"));
    }

    public List<Person> savePeople(List<Person> people) {
        if (people == null || people.isEmpty()) {
            throw new ValidationException("Список клиентов пустой");
        }

        // Проверка и фильтрация с лямбдой
        List<Person> invalidPeople = people.stream()
                .filter(person -> person.getName() == null || person.getName().trim().isEmpty())
                .toList();

        if (!invalidPeople.isEmpty()) {
            throw new ValidationException("Некоторые клиенты имеют некорректные данные: " +
                    invalidPeople.stream()
                            .map(p -> "ID: " + p.getId())
                            .toList());
        }

        return personRepository.saveAll(people).stream()
                .peek(person -> personCache.putToIdCache(person.getId(), person))
                .toList();
    }

    public boolean deletePerson(Long id) {
        Person cachedPerson = personCache.getPersonByIdCache(id);
        if (cachedPerson != null) {
            personCache.removeFromIdCache(id);
        }

        if (!personRepository.existsById(id)) {
            throw new ResourceNotFoundException("Клиент с ID " + id + " не найден");
        }

        personRepository.deleteById(id);
        return true;
    }

    public Person updatePerson(Long id, Person personDetails) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Клиент с ID " + id + " не найден"));

        if (personDetails.getName() == null || personDetails.getName().trim().isEmpty()) {
            throw new ValidationException("Имя клиента обязательно");
        }

        person.setName(personDetails.getName());
        person.setPhoneNumber(personDetails.getPhoneNumber());
        person.setTrainer(personDetails.getTrainer());
        person.setGym(personDetails.getGym());

        Person updated = personRepository.save(person);
        personCache.putToIdCache(id, updated);
        return updated;
    }

    public Person assignTrainerToPerson(Long personId, Long trainerId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Клиент с ID " + personId + " не найден"));

        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Тренер с ID " + trainerId + " не найден"));

        person.setTrainer(trainer);
        Person updated = personRepository.save(person);
        personCache.putToIdCache(personId, updated);
        return updated;
    }

    public Person assignGymToPerson(Long personId, Long gymId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Клиент с ID " + personId + " не найден"));

        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new ResourceNotFoundException("Спортзал с ID " + gymId + " не найден"));

        person.setGym(gym);
        Person updated = personRepository.save(person);
        personCache.putToIdCache(personId, updated);
        return updated;
    }

    public List<Person> getPersonsByGymType(String gymType) {
        return personRepository.findByGymType(gymType);
    }

    public List<Person> getPersonsByGymTypeNative(String gymType) {
        return personRepository.findPersonsByGymTypeNative(gymType);
    }

    public List<Person> getAllPeople() {
        return personRepository.findAll();
    }
}
