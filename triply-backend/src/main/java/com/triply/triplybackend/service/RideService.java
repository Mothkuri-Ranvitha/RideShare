package com.triply.triplybackend.service;

import com.triply.triplybackend.model.Ride;
import com.triply.triplybackend.model.User;
import com.triply.triplybackend.payload.requests.RideRequest;
import com.triply.triplybackend.repository.RideRepository;
import com.triply.triplybackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RideService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;

    public Ride postRide(Long driverId, RideRequest request) {

        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        Ride ride = new Ride();
        ride.setSource(request.getSource());
        ride.setDestination(request.getDestination());
        ride.setDepartureTime(request.getDepartureTime());
        ride.setAvailableSeats(request.getAvailableSeats());
        ride.setSourceLat(request.getSourceLat());
        ride.setSourceLng(request.getSourceLng());
        ride.setDestLat(request.getDestLat());
        ride.setDestLng(request.getDestLng());

        double fare = request.getFarePerSeat();
        if (fare <= 0 && request.getSourceLat() != null && request.getSourceLng() != null
                && request.getDestLat() != null && request.getDestLng() != null) {
            double distanceKm = haversine(request.getSourceLat(), request.getSourceLng(), request.getDestLat(), request.getDestLng());
            double basePerKm = 5.0; // simple base rate per km
            fare = Math.max(1.0, Math.round(distanceKm * basePerKm));
        }
        ride.setFarePerSeat(fare);
        ride.setDriver(driver);

        return rideRepository.save(ride);
    }

    public List<Ride> searchRide(String source, String destination, java.time.LocalDateTime date,
                                 Double minFare, Double maxFare, String vehicleModel) {
        List<Ride> base;
        if (date != null) {
            var start = date.toLocalDate().atStartOfDay();
            var end = start.plusDays(1);
            base = rideRepository.findBySourceIgnoreCaseAndDestinationIgnoreCaseAndDepartureTimeBetween(source, destination, start, end);
        } else {
            base = rideRepository.findBySourceIgnoreCaseAndDestinationIgnoreCase(source, destination);
        }

        return base.stream()
                .filter(r -> minFare == null || r.getFarePerSeat() >= minFare)
                .filter(r -> maxFare == null || r.getFarePerSeat() <= maxFare)
                .filter(r -> vehicleModel == null || r.getDriver() != null && r.getDriver().getVehicleModel() != null && r.getDriver().getVehicleModel().toLowerCase().contains(vehicleModel.toLowerCase()))
                .toList();
    }

    public List<Ride> findAll() {
        return rideRepository.findAll();
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
