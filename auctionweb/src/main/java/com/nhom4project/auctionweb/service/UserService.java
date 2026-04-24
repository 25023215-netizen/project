package com.nhom4project.auctionweb.service;

import com.nhom4project.auctionweb.data.dto.SignupRequest;
import com.nhom4project.auctionweb.data.model.Bidder;
import com.nhom4project.auctionweb.data.model.Roles;
import com.nhom4project.auctionweb.data.model.User;
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

        // Creating a Bidder by default
        User user = new Bidder();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword()); 
        user.setFullname(request.getFullname());
        user.setEmail(request.getEmail());
        user.setRole(Roles.BIDDER); 

        userRepository.save(user);
    }

    public User authenticate(String username, String password) throws Exception {
        User user = userRepository.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            throw new Exception("Invalid username or password!");
        }
        return user;
    }
}
