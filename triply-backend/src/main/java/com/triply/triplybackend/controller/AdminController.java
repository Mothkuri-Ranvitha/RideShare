package com.triply.triplybackend.controller;

import com.triply.triplybackend.model.ERole;
import com.triply.triplybackend.model.User;
import com.triply.triplybackend.repository.UserRepository;
import com.triply.triplybackend.repository.RideRepository;
import com.triply.triplybackend.repository.BookingRepository;
import com.triply.triplybackend.repository.PaymentRepository;
import com.triply.triplybackend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository userRepository;
    @Autowired private RideRepository rideRepository;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private PaymentRepository paymentRepository;

    private boolean isAdmin(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) return false;
        String token = header.substring(7);
        if (!jwtUtil.validate(token)) return false;
        String role = jwtUtil.getClaims(token).get("role", String.class);
        return role != null && role.equals(ERole.ROLE_ADMIN.name());
    }

    @GetMapping("/users")
    public ResponseEntity<?> users(HttpServletRequest req) {
        if (!isAdmin(req)) return ResponseEntity.status(403).body("Forbidden");
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PostMapping("/users/{id}/block")
    public ResponseEntity<?> blockUser(@PathVariable Long id, @RequestParam boolean blocked, HttpServletRequest req) {
        if (!isAdmin(req)) return ResponseEntity.status(403).body("Forbidden");
        var u = userRepository.findById(id);
        if (u.isEmpty()) return ResponseEntity.status(404).body("User not found");
        u.get().setBlocked(blocked);
        userRepository.save(u.get());
        return ResponseEntity.ok(u.get());
    }

    @PostMapping("/users/{id}/verify-driver")
    public ResponseEntity<?> verifyDriver(@PathVariable Long id, HttpServletRequest req) {
        if (!isAdmin(req)) return ResponseEntity.status(403).body("Forbidden");
        var u = userRepository.findById(id);
        if (u.isEmpty()) return ResponseEntity.status(404).body("User not found");
        u.get().setDriverVerified(true);
        userRepository.save(u.get());
        return ResponseEntity.ok(u.get());
    }

    @GetMapping("/rides")
    public ResponseEntity<?> rides(HttpServletRequest req) {
        if (!isAdmin(req)) return ResponseEntity.status(403).body("Forbidden");
        return ResponseEntity.ok(rideRepository.findAll());
    }

    @GetMapping("/bookings")
    public ResponseEntity<?> bookings(HttpServletRequest req) {
        if (!isAdmin(req)) return ResponseEntity.status(403).body("Forbidden");
        return ResponseEntity.ok(bookingRepository.findAll());
    }

    @GetMapping("/payments")
    public ResponseEntity<?> payments(HttpServletRequest req) {
        if (!isAdmin(req)) return ResponseEntity.status(403).body("Forbidden");
        return ResponseEntity.ok(paymentRepository.findAll());
    }
}

