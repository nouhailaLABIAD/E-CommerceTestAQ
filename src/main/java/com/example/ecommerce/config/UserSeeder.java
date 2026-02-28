package com.example.ecommerce.config;

import com.example.ecommerce.entity.Role;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserSeeder {

    @Bean
    CommandLineRunner initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if(userRepository.count() == 0) {
                User admin = new User();
                admin.setNom("Admin");
                admin.setEmail("admin@example.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);

                User client = new User();
                client.setNom("Client");
                client.setEmail("client@example.com");
                client.setPassword(passwordEncoder.encode("client123"));
                client.setRole(Role.CLIENT);
                userRepository.save(client);
            }
        };
    }
}