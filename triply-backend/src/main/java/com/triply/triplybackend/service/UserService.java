package com.triply.triplybackend.service;

import com.triply.triplybackend.model.User;
import com.triply.triplybackend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired private UserRepository repo;
    @Autowired private BCryptPasswordEncoder encoder;

    public User register(User u) {
        u.setPassword(encoder.encode(u.getPassword()));
        return repo.save(u);
    }

    public Optional<User> find(String email) {
        return repo.findByEmail(email);
    }
}
