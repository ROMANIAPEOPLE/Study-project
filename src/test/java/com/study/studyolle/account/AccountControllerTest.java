package com.study.studyolle.account;

import com.study.studyolle.domain.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @MockBean
    JavaMailSender javaMailSender;

    @Autowired private AccountRepository accountRepository;
    @Autowired private MockMvc mockMvc;


    @DisplayName("인증 메일 확인 - 오류")
    @Test
    void checkEMailToken_with_wrong_input() throws Exception {
        mockMvc.perform(get("/check-email-token")
                .param("token", "sadiasdasd")
                .param("email", "email.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"));
    }
    @Transactional
    @DisplayName("인증 메일 확인 - 정상" )
    @Test
    void checkEmailToken_with_success() throws  Exception{
        Account account = Account.builder()
                .email("jungkh405@naver.com")
                .nickname("김문섭섭")
                .password("123456789")
                .build();


        Account newAccount = accountRepository.save(account);
        newAccount.generateEmailCheckToken();

        mockMvc.perform(get("/check-email-token")
                .param("token", newAccount.getEmailCheckToken())
                .param("email", newAccount.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(authenticated());
    }


    @DisplayName("회원가입 화면 보이는지 테스트")
    @Test
    void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"));

    }

    @DisplayName("회원가입 처리 - 입력값 오류")
    @Test
    void signUpSubmit_wrong_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname","KI hyuk")
                .param("email", "email...")
                .param("password","12345")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"));
    }

    @DisplayName("회원가입 처리 - 정상 처리")
    @Test
    void signUpSubmit_success() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname","kihyuk")
                .param("email", "jungkh405@naver.com")
                .param("password","123456789")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));


        Account account =accountRepository.findByEmail("jungkh405@naver.com");
        assertNotNull(account);
        assertNotEquals(account.getPassword(), "123456789");



        then(javaMailSender).should().send(any(SimpleMailMessage.class));

    }
}