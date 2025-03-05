package com.ssw.lab4.controllers;

import com.ssw.lab4.services.PetNotFoundException;
import com.ssw.lab4.services.PetService;
import com.ssw.lab4.types.Pet;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v3/pet")
public class PetController {
    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping
    public ResponseEntity<Pet> addPet(@RequestBody Pet pet) {
        Pet savedPet = petService.addPet(pet);
        return ResponseEntity.ok(savedPet);
    }

    @PutMapping("/{petId}")
    public ResponseEntity<Pet> updatePet(
            @PathVariable Long petId,
            @RequestBody Pet pet) {
        Pet updatedPet = petService.updatePet(petId, pet);
        return ResponseEntity.ok(updatedPet);
    }

    @GetMapping("/{petId}")
    public ResponseEntity<Pet> getPetById(@PathVariable Long petId) {
        Pet pet = petService.getPetById(petId);
        return ResponseEntity.ok(pet);
    }

    @DeleteMapping("/{petId}")
    public ResponseEntity<Void> deletePet(@PathVariable Long petId) {
        petService.deletePet(petId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(PetNotFoundException.class)
    public ResponseEntity<String> handlePetNotFound(PetNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
