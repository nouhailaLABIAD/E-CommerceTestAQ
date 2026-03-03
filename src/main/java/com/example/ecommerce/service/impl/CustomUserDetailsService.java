package com.example.ecommerce.service.impl;

import com.example.ecommerce.entity.User;
import com.example.ecommerce.entity.Role;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.dto.RegisterDTO;

import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailsService(UserRepository userRepository,
                                    PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // LOGIN
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Utilisateur non trouvé : " + email));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }

    // REGISTER
    public void registerUser(RegisterDTO registerDTO) {

        // Vérifier si email existe déjà
        if (userRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email déjà utilisé !");
        }

        User user = new User();
        user.setNom(registerDTO.getNom());
        user.setEmail(registerDTO.getEmail());

        // Encodage du mot de passe
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));

        // Rôle par défaut = CLIENT
        user.setRole(Role.CLIENT);

        userRepository.save(user);
    }
}