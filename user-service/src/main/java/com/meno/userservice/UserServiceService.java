package com.meno.userservice;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceService {

    @Autowired
    private UserServiceRepository userRepository;

    public UserModel createUser(UserModel user) {
        if (user.getUsername() == null || user.getEmail() == null) {
            throw new IllegalArgumentException("Name and email are required fields." + user.getUsername() + user.getEmail());
        }
        return userRepository.save(user);
    }

    public Optional<UserModel> getUserById(Long id) {

        return userRepository.findById(id);
    }

    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("User with ID " + id + " does not exist.");
        }
    }

    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }

    public UserModel updateUser(UserModel updatedUser) {
        Optional<UserModel> existingUserOpt = userRepository.findById(updatedUser.getId());
        if (existingUserOpt.isEmpty()) {
            throw new EntityNotFoundException("User with ID " + updatedUser.getId() + " does not exist.");
        }
        UserModel existingUser = existingUserOpt.get();

        if (updatedUser.getUsername() != null) {
            existingUser.setUsername(updatedUser.getUsername());
        }
        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getPassword() != null) {
            existingUser.setPassword(updatedUser.getPassword());
        }
        return userRepository.save(existingUser);
    }
}