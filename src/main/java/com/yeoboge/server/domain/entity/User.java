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

    /**
     * 기존 회원의 정보가 담긴 {@link User} 엔티티에서 비밀번호만을 바꾸고자하는 비밀번호로 변경한
     * 새로운 User 엔티티를 반환함
     *
     * @param user 비밀번호 변경을 하고자 하는 회원 엔티티
     * @param password 변경하고자 하는 새로운 비밀번호
     * @return {@link User} 엔티티
     */
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

    /**
     * 기존 회원의 정보가 담긴 {@link User} 엔티티에서 프로필 사진 링크와 닉네임을 변경한
     * 새로운 User 엔티티를 반환함
     *
     * @param user 회원 정보 변경을 하고자 하는 회원 엔티티
     * @param path 변경하고자 하는 사진의 링크
     * @param nickname 변경하고자 하는 새로운 닉네임
     * @return {@link User} 엔티티
     */
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
