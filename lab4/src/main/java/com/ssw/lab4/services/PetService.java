package com.ssw.lab4.services;

import com.ssw.lab4.repositories.PetRepository;
import com.ssw.lab4.types.Pet;
import org.springframework.stereotype.Service;

@Service
public class PetService {
    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public Pet addPet(Pet pet) {
        return petRepository.save(pet);
    }

    public Pet updatePet(Long id, Pet petDetails) {
        return petRepository.findById(id)
                .map(existingPet -> {
                    existingPet.setName(petDetails.getName());
                    existingPet.setCategory(petDetails.getCategory());
                    existingPet.setTags(petDetails.getTags());
                    existingPet.setStatus(petDetails.getStatus());
                    return petRepository.save(existingPet);
                })
                .orElseThrow(() -> new PetNotFoundException(id));
    }

    public Pet getPetById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new PetNotFoundException(id));
    }

    public void deletePet(Long id) {
        petRepository.deleteById(id);
    }
}
