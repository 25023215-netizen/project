package com.nhom4project.auctionweb.server.service;

import com.nhom4project.auctionweb.server.dto.SignupRequest;
import com.nhom4project.auctionweb.server.model.*;
import com.nhom4project.auctionweb.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Đăng ký người dùng mới với lựa chọn role (BIDDER hoặc SELLER).
     * Admin không được phép đăng ký từ giao diện.
     */
    public void registerUser(SignupRequest request) throws Exception {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new Exception("Username is already taken!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new Exception("Email is already in use!");
        }

        String role = request.getRole() != null ? request.getRole().toUpperCase() : "BIDDER";

        // Không cho phép đăng ký Admin từ giao diện
        if ("ADMIN".equals(role)) {
            throw new Exception("Cannot register as Admin!");
        }

        User user;
        if ("SELLER".equals(role)) {
            Seller seller = new Seller();
            seller.setStoreName(request.getUsername() + "'s Store");
            user = seller;
            user.setRole(Roles.SELLER);
        } else {
            user = new Bidder();
            user.setRole(Roles.BIDDER);
        }

        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFullname(request.getFullname());
        user.setEmail(request.getEmail());

        userRepository.save(user);
    }

    /**
     * Xác thực đăng nhập. Kiểm tra thêm trạng thái khóa tài khoản.
     */
    public User authenticate(String username, String password) throws Exception {
        User user = userRepository.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            throw new Exception("Invalid username or password!");
        }
        if (user.isLocked()) {
            throw new Exception("This account has been locked by an admin!");
        }
        return user;
    }

    // ==================== Admin APIs ====================

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found!"));
        if (user instanceof Admin) {
            throw new Exception("Cannot delete Admin account!");
        }
        userRepository.delete(user);
    }

    public void toggleLockUser(Long userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found!"));
        if (user instanceof Admin) {
            throw new Exception("Cannot lock Admin account!");
        }
        user.setLocked(!user.isLocked());
        userRepository.save(user);
    }
}
