package com.study.studyolle.setting;

import com.study.studyolle.account.AccountService;
import com.study.studyolle.account.CurrentUser;
import com.study.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class SettingController {


    private final AccountService accountService;

    //어차피 자기 자신것만 수정 가능하기때문에, 어떤 유저인지 받아올 필요가 없음.
    @GetMapping("/settings/profile")
    public String profileUpdateForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new Profile(account));

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

}
