package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "social_accounts")
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocialAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String provider;

    @Column(name = "provider_id")
    private String providerId;

    private String email;

    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
