package com.nhom4project.auctionweb.data.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "Users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class User extends BaseEntity {

    @NotBlank(message = "Full name is required")
    private String fullname;

    @Column(unique = true, nullable = false)
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @Enumerated(EnumType.STRING)
    private Roles role;

    @Column(unique = true, nullable = false)
    private String username;

    // Standard getters/setters are handled by Lombok @Data
    // But if they were manually defined, I'll keep them or move to Lombok.
    // The previous version had manual getters/setters. I'll use Lombok to keep it clean.
}
