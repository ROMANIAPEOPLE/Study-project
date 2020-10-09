package com.study.studyolle.setting.validator;

import com.study.studyolle.account.AccountRepository;
import com.study.studyolle.domain.Account;
import com.study.studyolle.setting.form.NicknameForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class NicknameValidator implements Validator {

    private final AccountRepository accountRepository;
    @Override
    public boolean supports(Class<?> clazz) {
        return NicknameForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        NicknameForm nicknameForm = (NicknameForm)target;
        Account byNickname = accountRepository.findByNickname(nicknameForm.getNickname());
        if(byNickname != null){
            errors.rejectValue("nickname", "wrong.value", "입력하신 닉네임은 이미 사용중인 닉네임 입니다.");
        }

    }
}
