package com.gym.management.gymmanager.service;

import com.gym.management.gymmanager.cache.PersonCache;
import com.gym.management.gymmanager.model.Gym;
import com.gym.management.gymmanager.model.Person;
import com.gym.management.gymmanager.model.Trainer;
import com.gym.management.gymmanager.repository.GymRepository;
import com.gym.management.gymmanager.repository.PersonRepository;
import com.gym.management.gymmanager.repository.TrainerRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService {
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonCache personCache;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private GymRepository gymRepository;

    public Person savePerson(Person person) {
        Person saved = personRepository.save(person);
        personCache.putToIdCache(saved.getId(), saved); // Кэшируем новый объект
        return saved;
    }

    public Person getPersonById(Long id) {
        // Проверка кэша
        Person cachedPerson = personCache.getPersonByIdCache(id);
        if (cachedPerson != null) {
            return cachedPerson;
        }

        Optional<Person> personOpt = personRepository.findById(id);
        if (personOpt.isPresent()) {
            Person person = personOpt.get();
            personCache.putToIdCache(id, person); // Добавляем в кэш
            return person;
        }
        return null;
    }

    public boolean deletePerson(Long id) {
        if (personRepository.existsById(id)) {
            personRepository.deleteById(id);
            personCache.removeFromIdCache(id); // Удаляем из кэша
            return true;
        }
        return false;
    }

    public Person updatePerson(Long id, Person personDetails) {
        Optional<Person> optionalPerson = personRepository.findById(id);
        if (optionalPerson.isPresent()) {
            Person person = optionalPerson.get();
            person.setName(personDetails.getName());
            person.setPhoneNumber(personDetails.getPhoneNumber());
            person.setTrainer(personDetails.getTrainer());
            person.setGym(personDetails.getGym());

            // Сохраняем обновленные данные
            Person updated = personRepository.save(person);

            personCache.putToIdCache(id, updated);
            return updated;
        }
        return null;
    }

    public Person assignTrainerToPerson(Long personId, Long trainerId) {
        Optional<Person> personOpt = personRepository.findById(personId);
        if (personOpt.isPresent()) {
            Person person = personOpt.get();
            Optional<Trainer> trainerOpt = trainerRepository.findById(trainerId);
            if (trainerOpt.isPresent()) {
                person.setTrainer(trainerOpt.get());
                Person updated = personRepository.save(person);
                personCache.putToIdCache(personId, updated); // Обновляем кэш
                return updated;
            }
        }
        return null;
    }

    public Person assignGymToPerson(Long personId, Long gymId) {
        Optional<Person> personOpt = personRepository.findById(personId);
        if (personOpt.isPresent()) {
            Person person = personOpt.get();
            Optional<Gym> gymOpt = gymRepository.findById(gymId);
            if (gymOpt.isPresent()) {
                person.setGym(gymOpt.get());
                Person updated = personRepository.save(person);
                personCache.putToIdCache(personId, updated); // Обновляем кэш
                return updated;
            }
        }
        return null;
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
