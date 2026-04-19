package com.nhom4project.auctionweb.data.model;

import jakarta.persistence.*; //allow java to talk with database
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Table(name = "Users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // đánh dấu 1,2,3... tự động tăng
    private Long id; // SQL usually uses Long/BigInt for IDs instead of String

    @NotBlank(message = "Full name is required")
    private String fullname;

    @Column(unique = true, nullable = false) // SQL constraint for unique emails
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @Enumerated(EnumType.STRING) // Saves "USER" as a string in the DB
    private Roles role = Roles.BIDDER;

    @Column(unique = true, nullable = false)
    private String username;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Roles getRole() { return role; }
    public void setRole(Roles role) { this.role = role; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
