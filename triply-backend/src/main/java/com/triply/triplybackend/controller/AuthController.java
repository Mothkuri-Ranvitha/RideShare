package com.triply.triplybackend.controller;

import com.triply.triplybackend.model.*;
import com.triply.triplybackend.security.JwtUtil;
import com.triply.triplybackend.service.UserService;
import com.triply.triplybackend.payload.requests.*;
import com.triply.triplybackend.payload.responses.JwtResponse;
import com.triply.triplybackend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private UserService userService;
    @Autowired private UserRepository repo;
    @Autowired private JwtUtil jwt;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest r) {

        if (repo.existsByEmail(r.getEmail()))
            return ResponseEntity.badRequest().body("Email already exists");

        User u = new User();
        u.setEmail(r.getEmail());
        u.setPassword(r.getPassword());
        u.setName(r.getName());
        u.setPhone(r.getPhone());

        if (r.getRole().equalsIgnoreCase("DRIVER")) {
            u.setRole(ERole.ROLE_DRIVER);
            u.setVehicleModel(r.getVehicleModel());
            u.setLicensePlate(r.getLicensePlate());
            u.setCapacity(r.getCapacity());
        } else {
            u.setRole(ERole.ROLE_PASSENGER);
        }

        User saved = userService.register(u);

        String token = jwt.generateToken(saved.getEmail(), saved.getId(), saved.getRole().name());

        return ResponseEntity.ok(
                new JwtResponse(token, "Bearer", saved.getId(), saved.getEmail(), saved.getRole().name(), saved.getName())
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest r) {

        Optional<User> u = repo.findByEmail(r.getEmail());
        if (u.isEmpty()) return ResponseEntity.status(401).body("Invalid credentials");

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(r.getPassword(), u.get().getPassword()))
            return ResponseEntity.status(401).body("Invalid credentials");

        User user = u.get();
        String token = jwt.generateToken(user.getEmail(), user.getId(), user.getRole().name());

        return ResponseEntity.ok(
                new JwtResponse(token, "Bearer", user.getId(), user.getEmail(), user.getRole().name(), user.getName())
        );
    }
}
