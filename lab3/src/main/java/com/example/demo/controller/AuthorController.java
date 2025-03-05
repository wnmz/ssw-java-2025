package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class AuthorController {

    @GetMapping("/")
    public String index() {
        return "Made by Parkov Daniil, 2025";
    }
}