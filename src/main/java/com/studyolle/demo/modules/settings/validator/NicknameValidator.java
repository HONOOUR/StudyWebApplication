package com.studyolle.demo.modules.settings.validator;

import com.studyolle.demo.modules.account.AccountRepository;
import com.studyolle.demo.modules.account.Account;
import com.studyolle.demo.modules.settings.form.NicknameForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class NicknameValidator implements Validator {
    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return NicknameForm.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        NicknameForm nicknameForm = (NicknameForm)target;
        Account byNickname = accountRepository.findByNickname(nicknameForm.getNickname());
        if (byNickname != null) {
            errors.rejectValue("nickname", "wrong.value", "입력하신 닉네임을 사용하실 수 없습니다.");
        }
    }
}
