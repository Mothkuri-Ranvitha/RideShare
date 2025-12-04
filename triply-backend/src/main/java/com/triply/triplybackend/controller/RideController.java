package com.triply.triplybackend.controller;

import com.triply.triplybackend.model.Ride;
import com.triply.triplybackend.payload.requests.RideRequest;
import com.triply.triplybackend.security.JwtUtil;
import com.triply.triplybackend.service.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    @Autowired
    private RideService rideService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/post")
    public ResponseEntity<?> postRide(@RequestBody RideRequest request, HttpServletRequest httpReq) {
        String authHeader = httpReq.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validate(token)) {
            return ResponseEntity.status(401).body("Invalid token");
        }
        Long driverId = jwtUtil.getClaims(token).get("uid", Long.class);

        Ride ride = rideService.postRide(driverId, request);
        return ResponseEntity.ok(ride);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchRide(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Double minFare,
            @RequestParam(required = false) Double maxFare,
            @RequestParam(required = false) String vehicleModel
    ) {
        java.time.LocalDateTime dateParam = null;
        if (date != null && !date.isBlank()) {
            // Expect ISO date like 2025-12-02
            dateParam = java.time.LocalDate.parse(date).atStartOfDay();
        }
        List<Ride> rides = rideService.searchRide(source, destination, dateParam, minFare, maxFare, vehicleModel);
        return ResponseEntity.ok(rides);
    }

    @GetMapping
    public ResponseEntity<?> allRides() {
        return ResponseEntity.ok(rideService.findAll());
    }
}
