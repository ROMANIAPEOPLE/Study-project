package com.study.studyolle.account;

import com.study.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

@Controller
@RequiredArgsConstructor
public class AccountController {
    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    //signUpForm이라는 데이터를 받을때 함께 실행된다.
    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(signUpFormValidator);
    }


    @GetMapping("/sign-up")
    public String signUp(Model model){

        model.addAttribute(new SignUpForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors){
        if(errors.hasErrors()) {
            return "account/sign-up";
        }

        Account account = accountService.processNewAccount(signUpForm);
        accountService.login(account);


//        대체 가능
//        signUpFormValidator.validate(signUpForm,errors);
//        if(errors.hasErrors()) {
//            return "account/sign-up";
//        }

        return "redirect:/";
    }

    //이메일 재인증
    @GetMapping("/check-email")
    public String emailReSendPage(@CurrentUser Account account, Model model){


        model.addAttribute("email",account.getEmail());
        return "account/check-email";

    }

    @GetMapping("/resend-confirm-email")
    public String emailResend(@CurrentUser Account account, Model model){
        //토큰을 재생성하는것이 아니라, 기존의 토큰으로 다시 이메일 전송
        System.out.println("이메일 재전송");

        if(!account.canSendConfirmEmail()){
            model.addAttribute("error", "인증 메일 발송은 1시간에 1회만 가능합니다.");
            model.addAttribute("email",account.getEmail());
            return "account/check-email";
        }
        accountService.sendSignUpEmail(account);

        return "redirect:/";
    }

    //이메일 인증
    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model){
        Account account = accountRepository.findByEmail(email);
        System.out.println("email = " +  account.getEmail());
        System.out.println("nickname = " + account.getNickname());
        if( account == null) {
            model.addAttribute("error", "wrong.email");
            return "account/checked-email";
        }

        if(!account.isValidToken(token)){
            model.addAttribute("error", "wrong.token");
            return "account/checked-email";
          }


        accountService.completeSignUp(account);


        model.addAttribute("numberOfUser" ,accountRepository.count());
        model.addAttribute("nickname", account.getNickname());

        return "account/checked-email";
    }

    @GetMapping("/profile/{nickname}")
    public String viewProfile(@PathVariable String nickname, Model model, @CurrentUser Account account){
        Account byNickname = accountRepository.findByNickname(nickname);
        if(byNickname == null) {
            throw new IllegalArgumentException(nickname + "에 해당하는 사용자가 없습니다.");
        }

        model.addAttribute(byNickname);
        // model.addAttribute("account",byNickname") 와 동일함.
        model.addAttribute("isOwner", byNickname.equals(account));
        return "account/profile";
    }




}
