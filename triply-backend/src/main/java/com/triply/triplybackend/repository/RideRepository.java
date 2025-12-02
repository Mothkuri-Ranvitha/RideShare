package com.triply.triplybackend.repository;

import com.triply.triplybackend.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RideRepository extends JpaRepository<Ride, Long> {

    List<Ride> findBySourceIgnoreCaseAndDestinationIgnoreCase(String source, String destination);

    List<Ride> findBySourceIgnoreCaseAndDestinationIgnoreCaseAndDepartureTimeBetween(
            String source, String destination, LocalDateTime start, LocalDateTime end);
}
