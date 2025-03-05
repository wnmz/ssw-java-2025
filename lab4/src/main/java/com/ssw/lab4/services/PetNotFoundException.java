package com.ssw.lab4.services;

public class PetNotFoundException extends RuntimeException {
    public PetNotFoundException(Long id) {
        super("Pet not found with id " + id);
    }
}
