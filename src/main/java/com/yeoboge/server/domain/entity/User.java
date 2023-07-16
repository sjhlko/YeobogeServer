package com.yeoboge.server.domain.entity;

import com.yeoboge.server.domain.entity.converter.UserRoleConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Set;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    private String email;
    private String password;
    private String nickname;
    @Column(name = "user_code")
    private String userCode;
    @Convert(converter = UserRoleConverter.class)
    private Role role;

    @ManyToMany
    @JoinTable(
            name = "favorite_genre",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    Set<Genre> favoriteGenres;
}
