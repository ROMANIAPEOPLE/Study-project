package com.study.studyolle.setting;

import com.study.studyolle.WithAccount;
import com.study.studyolle.account.AccountRepository;
import com.study.studyolle.account.AccountService;
import com.study.studyolle.account.SignUpForm;
import com.study.studyolle.domain.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingControllerTest {


    @Autowired
    MockMvc mockMvc;


    @Autowired
    AccountRepository accountRepository;


    @AfterEach
    void afterEach () {
        accountRepository.deleteAll();
    }


    @WithAccount("jungkh405")
    @DisplayName("프로필 수정 - 입력값 정상")
    @Test
    void updateProfile() throws Exception{
        mockMvc.perform(post("/settings/profile")
        .param("bio", "짧은 소개를 수정하는 경우")
        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/profile"))
                .andExpect(flash().attributeExists("message"));
        Account kh = accountRepository.findByNickname("jungkh405");
    assertEquals("짧은 소개를 수정하는 경우", kh.getBio());
    }

    @WithAccount("jungkh405")
    @DisplayName("프로필 수정 - 실패")
    @Test
    void updateProfile_error() throws  Exception{
        mockMvc.perform(post("/settings/profile")
                .param("bio","긴소개긴소개긴소개긴소개긴소긴소개긴소개긴소개긴소개긴소개긴소개긴소개긴소개긴소개긴소개긴소개긴소개")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/profile"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());
        Account kh = accountRepository.findByNickname("jungkh405");
        assertNull(kh.getBio());
    }

}