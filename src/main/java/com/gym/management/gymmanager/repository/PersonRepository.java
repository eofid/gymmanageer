package com.gym.management.gymmanager.repository;

import com.gym.management.gymmanager.model.Person;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    // JPQL запрос для нахождения людей по типу зала
    @Query("SELECT p FROM Person p WHERE p.gym.type = :gymType")
    List<Person> findByGymType(String gymType);

    // Native SQL запрос для нахождения людей по типу зала
    @Query(value = "SELECT * FROM person p JOIN gym g ON p.gym_id = g.id WHERE g.type = :gymType",
            nativeQuery = true)
    List<Person> findPersonsByGymTypeNative(String gymType);
}