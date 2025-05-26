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
    private static final String CLIENT_NOT_FOUND_PREFIX = "Клиент с ID ";
    private static final String NOT_FOUND_SUFFIX = " не найден";

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
                .orElseThrow(() -> new ResourceNotFoundException(CLIENT_NOT_FOUND_PREFIX + id + NOT_FOUND_SUFFIX));
    }

    public List<Person> savePeople(List<Person> people) {
        if (people == null || people.isEmpty()) {
            throw new ValidationException("Список клиентов пустой");
        }

        List<Person> invalidPeople = people.stream()
                .filter(person -> person.getName() == null || person.getName().trim().isEmpty())
                .toList();

        if (!invalidPeople.isEmpty()) {
            throw new ValidationException("Некоторые клиенты имеют некорректные данные: " +
                    invalidPeople.stream()
                            .map(p -> "ID: " + p.getId())
                            .toList());
        }

        List<Person> savedPeople = personRepository.saveAll(people);
        savedPeople.forEach(person -> personCache.putToIdCache(person.getId(), person));
        return savedPeople;
    }

    public boolean deletePerson(Long id) {
        Person cachedPerson = personCache.getPersonByIdCache(id);
        if (cachedPerson != null) {
            personCache.removeFromIdCache(id);
        }

        if (!personRepository.existsById(id)) {
            throw new ResourceNotFoundException(CLIENT_NOT_FOUND_PREFIX + id + NOT_FOUND_SUFFIX);
        }

        personRepository.deleteById(id);
        return true;
    }

    public Person updatePerson(Long id, Person personDetails) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CLIENT_NOT_FOUND_PREFIX + id + NOT_FOUND_SUFFIX));

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
                .orElseThrow(() -> new ResourceNotFoundException(CLIENT_NOT_FOUND_PREFIX + personId + NOT_FOUND_SUFFIX));

        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Тренер с ID " + trainerId + NOT_FOUND_SUFFIX));

        person.setTrainer(trainer);
        Person updated = personRepository.save(person);
        personCache.putToIdCache(personId, updated);
        return updated;
    }

    public Person assignGymToPerson(Long personId, Long gymId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException(CLIENT_NOT_FOUND_PREFIX + personId + NOT_FOUND_SUFFIX));

        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new ResourceNotFoundException("Спортзал с ID " + gymId + NOT_FOUND_SUFFIX));

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
