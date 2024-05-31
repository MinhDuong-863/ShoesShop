package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "fullname")
    private String fullName;

    @Column(name = "phone_number", length = 10)
    private String phoneNumber;

    private String address;

    @Column(nullable = false)
    private String password;

    @Column(name = "is_active")
    private boolean active;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column(name = "facebook_account_id")
    private int fbAccountId;

    @Column(name = "google_account_id")
    private int ggAccountId;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
}
