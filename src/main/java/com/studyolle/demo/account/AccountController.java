package com.studyolle.demo.account;

import com.studyolle.demo.ConsoleMailSender;
import com.studyolle.demo.domain.Account;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class AccountController {
    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

//    public AccountController(SignUpFormValidator signUpFormValidator, AccountRepository accountRepository, JavaMailSender javaMailSender) {
//        this.signUpFormValidator = signUpFormValidator;
//        this.accountRepository = accountRepository;
//        this.javaMailSender = javaMailSender;
//    }

    @InitBinder("signUpForm")
    public void InitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute(new SignUpForm()); // camel case를 사용한 클래스 이름
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors) {
        if (errors.hasErrors()) {
            return "account/sign-up"; // return form
        }
        accountService.processNewAccount(signUpForm);

        return "redirect:/";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model) {
        // find the user
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            model.addAttribute("error", "wrong.email");
            return "account/checkedEmail";
        }

        // compare tokens from account and email
        if (!account.getEmailCheckerToken().equals(token)) {
            model.addAttribute("error", "wrong.token");
            return "account/checkedEmail";
        }

        account.setEmailVerified(true);
        account.setJoinedAt(LocalDateTime.now());
        model.addAttribute("numberOfUesr", accountRepository.count());
        model.addAttribute("nickname", account.getNickname());
        return view;

    }

}
