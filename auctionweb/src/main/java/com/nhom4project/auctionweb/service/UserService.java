package com.nhom4project.auctionweb.service;

import com.nhom4project.auctionweb.data.dto.SignupRequest;
import com.nhom4project.auctionweb.data.model.Roles;
import com.nhom4project.auctionweb.data.model.Users;
import com.nhom4project.auctionweb.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void registerUser(SignupRequest request) throws Exception {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new Exception("Username is already taken!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new Exception("Email is already in use!");
        }

        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword()); // In a real app, hash password using PasswordEncoder
        user.setFullname(request.getFullname());
        user.setEmail(request.getEmail());
        user.setRole(Roles.BIDDER); // Default role

        userRepository.save(user);
    }

    public Users authenticate(String username, String password) throws Exception {
        Users user = userRepository.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            throw new Exception("Invalid username or password!");
        }
        return user;
    }
}
