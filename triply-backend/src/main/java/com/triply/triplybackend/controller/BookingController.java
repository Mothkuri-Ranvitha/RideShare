package com.triply.triplybackend.controller;

import com.triply.triplybackend.model.Booking;
import com.triply.triplybackend.model.User;
import com.triply.triplybackend.model.Payment;
import com.triply.triplybackend.repository.BookingRepository;
import com.triply.triplybackend.repository.RideRepository;
import com.triply.triplybackend.repository.UserRepository;
import com.triply.triplybackend.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @PostMapping("/book")
    public ResponseEntity<?> bookRide(@RequestBody Booking bookingRequest, Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String email = (String) auth.getPrincipal();
        var passengerOpt = userRepository.findByEmail(email);
        if (passengerOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }
        Long passengerId = passengerOpt.get().getId();

        var rideOpt = rideRepository.findById(bookingRequest.getRideId());
        if (rideOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Ride not found");
        }
        var ride = rideOpt.get();

        if (ride.getAvailableSeats() < bookingRequest.getSeatsBooked()) {
            return ResponseEntity.badRequest().body("Not enough seats available");
        }

        // reduce seats
        ride.setAvailableSeats(ride.getAvailableSeats() - bookingRequest.getSeatsBooked());
        rideRepository.save(ride);

        // save booking
        Booking booking = new Booking();
        booking.setRide(ride);
        booking.setPassenger(passengerOpt.get());
        booking.setSeatsBooked(bookingRequest.getSeatsBooked());
        booking.setStatus("CONFIRMED");

        bookingRepository.save(booking);

        // simulate payment capture
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getSeatsBooked() * ride.getFarePerSeat());
        payment.setStatus("PAID");
        paymentRepository.save(payment);

        return ResponseEntity.ok(booking);
    }

    @GetMapping("/my")
    public ResponseEntity<?> myBookings(Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        String email = (String) auth.getPrincipal();
        var passengerOpt = userRepository.findByEmail(email);
        if (passengerOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }
        Long passengerId = passengerOpt.get().getId();
        return ResponseEntity.ok(bookingRepository.findByPassengerId(passengerId));
    }
}
