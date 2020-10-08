package com.study.studyolle.setting;

import com.study.studyolle.account.AccountService;
import com.study.studyolle.account.CurrentUser;
import com.study.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class SettingController {
    //signUpForm이라는 데이터를 받을때 함께 실행된다.
    @InitBinder("passwordForm")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(new PasswordFormValidator());
    }

    private final AccountService accountService;
    private final ModelMapper modelMapper;

    //어차피 자기 자신것만 수정 가능하기때문에, 어떤 유저인지 받아올 필요가 없음.
    @GetMapping("/settings/profile")
    public String profileUpdateForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account,Profile.class));

        return "settings/profile";


    }

    @PostMapping("/settings/profile")
    public String updateProfile(@CurrentUser Account account, @Valid Profile profile, Errors errors, Model model
    , RedirectAttributes attributes){
        if(errors.hasErrors()){
            model.addAttribute(account);
            return "settings/profile";
        }

        accountService.updateProfile(account,profile);
        attributes.addFlashAttribute("message", "회원정보 수정이 완료되었습니다.");
        return "redirect:/settings/profile";
    }

    @GetMapping("/settings/password")
    public String updatePasswordForm(@CurrentUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(new PasswordForm());
        return "settings/password";
    }

    @PostMapping("/settings/password")
    public String updatePassword(@CurrentUser Account account, @Valid PasswordForm passwordForm, Errors errors, Model model,RedirectAttributes redirectAttributes){
        if(errors.hasErrors()){
            model.addAttribute("account");
            return "settings/password";
        }

        accountService.updatePassword(account, passwordForm.getNewPassword());
        redirectAttributes.addFlashAttribute("message", "패스워드가 정상적으로 변경되었습니다.");

        return "redirect:/settings/password";
    }


    @GetMapping("/settings/notifications")
    public String notificationsPage(@CurrentUser Account account, Model model){
        model.addAttribute(modelMapper.map(account,Notifications.class));
        model.addAttribute(account);
        return "settings/notifications";
    }
    @PostMapping("/settings/notifications")
    public String notifications(@CurrentUser Account account, Model model, @Valid Notifications notifications,Errors errors, RedirectAttributes redirectAttributes){
        if(errors.hasErrors()){
            model.addAttribute(account);
            return "settings/notifications";
        }

        accountService.updateNotifications(account,notifications);

        redirectAttributes.addFlashAttribute("message", "정상적으로 변경되었습니다.");

        return "redirect:/settings/notifications";


    }


}

