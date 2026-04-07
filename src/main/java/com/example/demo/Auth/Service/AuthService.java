package com.example.demo.Auth.Service;

import com.example.demo.Auth.Model.User;
import com.example.demo.Auth.Repo.UserRepo;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepo userRepo;

    public String register(String username, String password, String role) {
        if (userRepo.findByUsername(username).isPresent()) {
            return "Username already exists!";
        }
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User();
        user.setUsername(username);
        user.setPassword(hashedPassword);
        user.setRole(role);
        userRepo.save(user);
        return "Registration successful!";
    }

    public String login(String username, String password) {
        Optional<User> userOpt = userRepo.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (BCrypt.checkpw(password, user.getPassword())) {
                return "Login successful! Welcome " + username + " (" + user.getRole() + ")";
            } else {
                return "Invalid password.";
            }
        }
        return "User not found.";
    }
}
