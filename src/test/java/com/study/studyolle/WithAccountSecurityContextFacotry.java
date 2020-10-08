package com.study.studyolle;

import com.study.studyolle.account.AccountService;
import com.study.studyolle.account.SignUpForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithAccountSecurityContextFacotry implements WithSecurityContextFactory<WithAccount> {

    @Autowired
    AccountService accountService;
    @Override
    public SecurityContext createSecurityContext(WithAccount withAccount) {
        // 빈을 주입 받을 수 있다.

// Authentication 만들고 SecurityuContext에 넣어주기
        String nickname = withAccount.value();

        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname(nickname);
        signUpForm.setEmail("jungkh405@naver.com");
        signUpForm.setPassword("123123123");
        accountService.processNewAccount(signUpForm);

        UserDetails principal = accountService.loadUserByUsername(nickname);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        return  context;
    }

}
