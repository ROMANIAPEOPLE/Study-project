package com.study.studyolle.main;

import com.study.studyolle.account.AccountRepository;
import com.study.studyolle.account.AccountService;
import com.study.studyolle.account.SignUpForm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MainControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;
    @BeforeEach
    void beforeEach(){
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setEmail("jungkh405@naver.com");
        signUpForm.setNickname("jungkh405");
        signUpForm.setPassword("123123123");
        accountService.processNewAccount(signUpForm);
    }

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }


    @DisplayName("이메일로 로그인 성공")
    @Test
    void login_with_email() throws Exception {
        mockMvc.perform(post("/login")
        .param("username", "jungkh405@naver.com")
        .param("password", "123123123")
        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("jungkh405"));
    }


    @DisplayName("로그인 실패")
    @Test
    void login_fail() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", "jungkh")
                .param("password", "1231231233")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @DisplayName("닉네임 로그인 성공")
    @Test
    void login_with_nickname() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", "jungkh405")
                .param("password", "123123123")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("jungkh405"));
    }

    @DisplayName("로그아웃")
    @Test
    void logout() throws  Exception {
        mockMvc.perform(post("/logout")
        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated());
    }


}