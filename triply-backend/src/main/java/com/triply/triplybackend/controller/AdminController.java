package com.triply.triplybackend.controller;

import com.triply.triplybackend.model.User;
import com.triply.triplybackend.model.Ride;
import com.triply.triplybackend.model.Booking;
import com.triply.triplybackend.model.ERole;
import com.triply.triplybackend.payload.requests.AdminUpdateUserRequest;
import com.triply.triplybackend.repository.UserRepository;
import com.triply.triplybackend.repository.RideRepository;
import com.triply.triplybackend.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository users;

    @Autowired
    private RideRepository rides;

    @Autowired
    private BookingRepository bookings;

    @GetMapping("/users")
    public List<User> allUsers() {
        return users.findAll();
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody AdminUpdateUserRequest body) {
        return users.findById(id)
                .map(u -> {
                    if (body.getName() != null) u.setName(body.getName());
                    if (body.getPhone() != null) u.setPhone(body.getPhone());
                    if (body.getBlocked() != null) u.setBlocked(body.getBlocked());
                    if (body.getDriverVerified() != null) u.setDriverVerified(body.getDriverVerified());
                    if (body.getVehicleModel() != null) u.setVehicleModel(body.getVehicleModel());
                    if (body.getLicensePlate() != null) u.setLicensePlate(body.getLicensePlate());
                    if (body.getCapacity() != null) u.setCapacity(body.getCapacity());
                    if (body.getRole() != null) {
                        try {
                            u.setRole(ERole.valueOf(body.getRole()));
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                    users.save(u);
                    return ResponseEntity.ok(u);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!users.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        users.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{id}/block")
    public ResponseEntity<?> blockUser(@PathVariable Long id, @RequestParam boolean blocked) {
        return users.findById(id)
                .map(u -> {
                    u.setBlocked(blocked);
                    users.save(u);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/users/{id}/verify-driver")
    public ResponseEntity<?> verifyDriver(@PathVariable Long id) {
        return users.findById(id)
                .map(u -> {
                    u.setDriverVerified(true);
                    users.save(u);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/rides")
    public List<Ride> allRides() {
        return rides.findAll();
    }

    @GetMapping("/bookings")
    public List<Booking> allBookings() {
        return bookings.findAll();
    }

    @GetMapping("/payments")
    public List<Map<String, Object>> payments() {
        // Payments are not modeled yet; return empty list for now
        return Collections.emptyList();
    }
}

