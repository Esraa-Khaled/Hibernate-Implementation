package com.example.demo.controllers;


import com.example.demo.entities.User;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class userController {

    @Autowired
    UserService userService;

    @GetMapping("/getUsers")
    public Iterable<User> getAllUsers() throws Exception {
        return userService.getAllUsers();
    }

}