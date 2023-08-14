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
    private Set<Genre> favoriteGenres;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Friend> friends;

    /**
     * 기존 회원의 비밀번호만을 바꾸고자하는 비밀번호로 변경한다.
     *
     * @param password 변경하고자 하는 새로운 비밀번호
     */
    public void updatePassword(String password){
        this.password = password;
    }

    /**
     * 기존 회원의 프로필 사진 링크와 닉네임을 변경한다.
     *
     * @param path 변경하고자 하는 사진의 링크
     * @param nickname 변경하고자 하는 새로운 닉네임
     */
    public void updateUserProfile(String path, String nickname){
        this.profileImagePath = path;
        this.nickname = nickname;
    }
}
