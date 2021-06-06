package com.studyolle.demo.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Account {
    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    private boolean emailVerified;

    private String emailCheckerToken;

    private LocalDateTime joinedAt;

    private String bio;

    private String url;

    private String occupation;

    private String location; // varchar(255)

    @Lob @Basic(fetch = FetchType.EAGER) // text type larger than varchar
    private String profileImage;

    private boolean studyCreatedByEmail;

    private boolean studyCreatedByWeb;

    private boolean studyEnrollmentResultByEmail;

    private boolean studyEnrollmentResultByWeb;

    private boolean studyUpdateByEmail;

    private boolean studyUpdateByWeb;

    public void generateEmailCheckToken() {
        this.emailCheckerToken = UUID.randomUUID().toString();
    }

    public void completeSignUp() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }
}
