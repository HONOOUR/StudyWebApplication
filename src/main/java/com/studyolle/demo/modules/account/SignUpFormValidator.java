package com.studyolle.demo.modules.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class SignUpFormValidator implements Validator {
//    생성자 파라미터가 final이고 빈으로(Component) 등록되어로 있으면 생성자 생성하지않고 자동으로 추가됨
//    public SignUpFormValidator(AccountRepository accountRepository) {
//        this.accountRepository = accountRepository;
//    }

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        // sign-up form type instance 를
        return aClass.isAssignableFrom(SignUpForm.class);
    }

    @Override
    public void validate(Object object, Errors errors) {
        // access to nickname, email field in database
        SignUpForm signUpForm = (SignUpForm)object;
        if (accountRepository.existsByEmail(signUpForm.getEmail())) {
            errors.rejectValue("email", "invalid.email", new Object[]{signUpForm.getEmail()}, "이미 사용중인 이메일 입니다.");
        }

        if (accountRepository.existsByNickname(signUpForm.getNickname())) {
            errors.rejectValue("nickname", "invalid.nickname", new Object[]{signUpForm.getEmail()}, "이미 사용중인 닉네임 입니다.");
        }
    }
}
