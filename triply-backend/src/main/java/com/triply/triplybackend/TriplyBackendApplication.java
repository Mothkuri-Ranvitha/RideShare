package com.triply.triplybackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;

import com.triply.triplybackend.model.User;
import com.triply.triplybackend.model.ERole;
import com.triply.triplybackend.model.Ride;
import com.triply.triplybackend.repository.UserRepository;
import com.triply.triplybackend.repository.RideRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.time.LocalDateTime;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class TriplyBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(TriplyBackendApplication.class, args);
    }

    @Bean
    CommandLineRunner seed(
            @Autowired UserRepository users,
            @Autowired RideRepository rides,
            @Autowired BCryptPasswordEncoder encoder
    ) {
        return args -> {
            if (users.findByEmail("admin@example.com").isEmpty()) {
                User admin = new User();
                admin.setEmail("admin@example.com");
                admin.setPassword(encoder.encode("Admin123!"));
                admin.setName("Admin");
                admin.setRole(ERole.ROLE_ADMIN);
                users.save(admin);
            }

            users.findByEmail("vibha@example.com").orElseGet(() -> {
                User u = new User();
                u.setEmail("vibha@example.com");
                u.setPassword(encoder.encode("Password123!"));
                u.setName("Vibha");
                u.setPhone("9000000001");
                u.setRole(ERole.ROLE_PASSENGER);
                return users.save(u);
            });

            User ranvitha = users.findByEmail("ranvitha@example.com").orElseGet(() -> {
                User u = new User();
                u.setEmail("ranvitha@example.com");
                u.setPassword(encoder.encode("Password123!"));
                u.setName("Ranvitha");
                u.setPhone("9000000002");
                u.setRole(ERole.ROLE_DRIVER);
                u.setVehicleModel("Hyundai i20");
                u.setLicensePlate("KA-09-7777");
                u.setCapacity(3);
                u.setDriverVerified(true);
                return users.save(u);
            });

            users.findByEmail("alice@example.com").orElseGet(() -> {
                User u = new User();
                u.setEmail("alice@example.com");
                u.setPassword(encoder.encode("Password123!"));
                u.setName("Alice");
                u.setPhone("9000000003");
                u.setRole(ERole.ROLE_PASSENGER);
                return users.save(u);
            });

            User bob = users.findByEmail("bob@example.com").orElseGet(() -> {
                User u = new User();
                u.setEmail("bob@example.com");
                u.setPassword(encoder.encode("Password123!"));
                u.setName("Bob");
                u.setPhone("9000000004");
                u.setRole(ERole.ROLE_DRIVER);
                u.setVehicleModel("Maruti Swift");
                u.setLicensePlate("MH-12-1234");
                u.setCapacity(4);
                return users.save(u);
            });

            if (rides.count() == 0) {
                Ride r1 = new Ride();
                r1.setSource("Mumbai");
                r1.setDestination("Pune");
                r1.setDepartureTime(LocalDateTime.now().plusDays(1).withHour(9).withMinute(0));
                r1.setAvailableSeats(3);
                r1.setFarePerSeat(500);
                r1.setDriver(ranvitha);
                rides.save(r1);

                Ride r2 = new Ride();
                r2.setSource("Bengaluru");
                r2.setDestination("Mysuru");
                r2.setDepartureTime(LocalDateTime.now().plusDays(2).withHour(16).withMinute(0));
                r2.setAvailableSeats(4);
                r2.setFarePerSeat(450);
                r2.setDriver(bob);
                rides.save(r2);
            }
        };
    }
}
