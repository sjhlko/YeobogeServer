package com.yeoboge.server.domain.entity;

import com.yeoboge.server.domain.entity.converter.UserRoleConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE user SET deleted_at = current_timestamp WHERE user_id = ? ")
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
    private LocalDateTime deletedAt;
    @Column(columnDefinition = "TEXT")
    private String profileImagePath;

    @ManyToMany
    @JoinTable(
            name = "favorite_genre",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    Set<Genre> favoriteGenres;

    public static User updatePassword(User user, String password){
        return User.builder()
                .id(user.getId())
                .password(password)
                .email(user.getEmail())
                .nickname(user.getNickname())
                .favoriteGenres(user.getFavoriteGenres())
                .profileImagePath(user.getProfileImagePath())
                .role(user.getRole())
                .userCode(user.getUserCode())
                .build();
    }

    public static User updateUserProfile(User user, String path, String nickname){
        return User.builder()
                .id(user.getId())
                .password(user.getPassword())
                .email(user.getEmail())
                .nickname(nickname)
                .favoriteGenres(user.getFavoriteGenres())
                .role(user.getRole())
                .profileImagePath(path)
                .userCode(user.getUserCode())
                .build();
    }

}
