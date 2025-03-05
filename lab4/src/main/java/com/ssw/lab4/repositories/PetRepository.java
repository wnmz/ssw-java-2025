package com.ssw.lab4.repositories;

import com.ssw.lab4.types.Pet;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PetRepository {
    private final Map<Long, Pet> pets = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public Pet save(Pet pet) {
        if (pet.getId() == null) {
            pet.setId(idCounter.getAndIncrement());
        }
        pets.put(pet.getId(), pet);
        return pet;
    }

    public Optional<Pet> findById(Long id) {
        return Optional.ofNullable(pets.get(id));
    }

    public void deleteById(Long id) {
        pets.remove(id);
    }

    public List<Pet> findAll() {
        return new ArrayList<>(pets.values());
    }
}
