package com.TicketingApp.Service;

import com.TicketingApp.Entity.Users;
import com.TicketingApp.Entity.Users.Roles;
import com.TicketingApp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserManagementService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    public Users findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public Users registerUser(String username, String rawPassword, Roles role) {
        Users user = new Users();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        return userRepository.save(user);
    }

    public boolean resetPassword(Long userId, String newRawPassword) {
        Users user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(newRawPassword));
        userRepository.save(user);
        return true;
    }

    public boolean deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            return false;
        }
        userRepository.deleteById(userId);
        return true;
    }
}
