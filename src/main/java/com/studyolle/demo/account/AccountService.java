package com.studyolle.demo.account;

import com.studyolle.demo.ConsoleMailSender;
import com.studyolle.demo.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    public void processNewAccount(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        newAccount.generateEmailCheckToken();
        sendSignUpConfirmationEmail(newAccount);
    }

    private Account saveNewAccount(@Valid SignUpForm signUpForm) {
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname((signUpForm.getNickname()))
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .studyCreatedByWeb(true)
                .studyUpdateByWeb(true)
                .studyEnrollmentResultByWeb(true)
                .build();

        return accountRepository.save(account);
    }

    private void sendSignUpConfirmationEmail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("study, sign up completes");
        mailMessage.setText("/check-email-token=" + newAccount.getEmailCheckerToken() +
                "&email=" + newAccount.getEmail());
        javaMailSender.send(mailMessage);
    }
}
