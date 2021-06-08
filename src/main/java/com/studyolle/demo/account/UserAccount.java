package com.studyolle.demo.account;

import com.studyolle.demo.domain.Account;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

// Adapter class for Spring Security User and Application domain one
@Getter
public class UserAccount  extends User { // spring security

    private Account account; // domain

    public UserAccount(Account account) {
        super(account.getNickname(), account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.account = account;
    }
}
