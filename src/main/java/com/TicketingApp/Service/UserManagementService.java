package com.TicketingApp.Service;

import com.TicketingApp.Entity.UserVariable;
import com.TicketingApp.Entity.UserVariable.Role;
import com.TicketingApp.Repository.UserVariableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserManagementService {

    @Autowired
    private UserVariableRepository userVariableRepository;

    public List<UserVariable> getAllUsers() {
        return userVariableRepository.findAll();
    }

    public UserVariable findById(Long id) {
        return userVariableRepository.findById(id).orElse(null);
    }

    public UserVariable registerUser(String email, Role role) {
        UserVariable existing = userVariableRepository.findByEmail(email);
        if (existing != null) {
            existing.setRole(role);
            return userVariableRepository.save(existing);
        }
        UserVariable user = new UserVariable();
        user.setEmail(email);
        user.setRole(role);
        return userVariableRepository.save(user);
    }

    public boolean updateUserRole(Long id, Role role) {
        UserVariable user = userVariableRepository.findById(id).orElse(null);
        if (user == null) return false;
        user.setRole(role);
        userVariableRepository.save(user);
        return true;
    }

    public boolean deleteUser(Long id) {
        if (!userVariableRepository.existsById(id)) {
            return false;
        }
        userVariableRepository.deleteById(id);
        return true;
    }

    public List<String> getNotificationEmails() {
        return userVariableRepository.findByReceiveNotificationsTrue()
                .stream().map(UserVariable::getEmail).toList();
    }

    public boolean toggleNotifications(Long id) {
        UserVariable user = userVariableRepository.findById(id).orElse(null);
        if (user == null) return false;
        user.setReceiveNotifications(!user.isReceiveNotifications());
        userVariableRepository.save(user);
        return true;
    }
}
