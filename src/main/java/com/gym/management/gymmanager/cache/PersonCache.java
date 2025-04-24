package com.gym.management.gymmanager.cache;

import com.gym.management.gymmanager.model.Person;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PersonCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonCache.class);

    private static final int MAX_CACHE_SIZE = 100;

    private final Map<Long, Person> personByIdCache = Collections
            .synchronizedMap(new LinkedHashMap<>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, Person> eldest) {
            return size() > MAX_CACHE_SIZE;
        }
    });

    public void putToIdCache(Long id, Person person) {
        personByIdCache.put(id, person);
        LOGGER.info("Put person into ID cache: {}", person);
    }

    public Person getPersonByIdCache(Long id) {
        LOGGER.info("Getting person from ID cache with ID: {}", id);
        return personByIdCache.get(id);
    }

    public void removeFromIdCache(Long id) {
        LOGGER.info("Removing person with ID {} from ID cache", id);
        personByIdCache.remove(id);
    }

    public void clear() {
        LOGGER.info("Clearing ID cache");
        personByIdCache.clear();
    }

    public Map<Long, Person> getAll() {
        LOGGER.info("Getting all people from ID cache.");
        return new LinkedHashMap<>(personByIdCache);
    }
}
