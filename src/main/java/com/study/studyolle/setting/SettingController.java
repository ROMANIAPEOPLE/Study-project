package com.study.studyolle.setting;

import com.study.studyolle.account.AccountService;
import com.study.studyolle.account.CurrentUser;
import com.study.studyolle.domain.Account;
import com.study.studyolle.domain.Tag;
import com.study.studyolle.setting.form.*;
import com.study.studyolle.setting.validator.NicknameValidator;
import com.study.studyolle.setting.validator.PasswordFormValidator;
import com.study.studyolle.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class SettingController {

    private final TagRepository tagRepository;
    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private final NicknameValidator nicknameValidator;

    static final String SETTINGS_TAGS_VIEW_NAME = "settings/tags";
    static final String SETTINGS_TAGS_URL = "/" + SETTINGS_TAGS_VIEW_NAME;

    //signUpForm이라는 데이터를 받을때 함께 실행된다.
    @InitBinder("passwordForm")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(new PasswordFormValidator());
    }

    @InitBinder("nicknameForm")
    public void initBinderByNickname(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(nicknameValidator);
    }


    //어차피 자기 자신것만 수정 가능하기때문에, 어떤 유저인지 받아올 필요가 없음.
    @GetMapping("/settings/profile")
    public String profileUpdateForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, Profile.class));

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
        model.addAttribute(modelMapper.map(account, Notifications.class));
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

    @GetMapping("/settings/account")
    public String accountUpdatePage(@CurrentUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, NicknameForm.class));
        return "settings/account";
    }

    @PostMapping("/settings/account")
    public String accountUpdage(@CurrentUser Account account, Model model, @Valid NicknameForm nicknameForm, Errors errors
    , RedirectAttributes redirectAttributes){
        if(errors.hasErrors()) {
            model.addAttribute(account);
            return "settings/account";
        }

        accountService.updateAccount(account,nicknameForm);
        redirectAttributes.addFlashAttribute("message", "닉네임이 정상적으로 변경되었읍니다.");
        return "redirect:/settings/account";
    }

    @GetMapping(SETTINGS_TAGS_URL)
    public String updateTags(@CurrentUser Account account, Model model){
        model.addAttribute(account);
        Set<Tag> tags = accountService.getTag(account);
        model.addAttribute("tags", tags.stream().map(Tag::getTitle).collect(Collectors.toList()));
        return SETTINGS_TAGS_VIEW_NAME;
    }

    @PostMapping("/settings/tags/add")
    @ResponseBody
    public ResponseEntity addTag(@CurrentUser Account account, @RequestBody TagForm tagForm)
    {
        String title = tagForm.getTagTitle();
        Tag tag = tagRepository.findByTitle(title);
        if( tag == null) {
            tag = tagRepository.save(Tag.builder().title(tagForm.getTagTitle()).build());
        }
        accountService.addTag(account,tag);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/settings/tags/remove")
    @ResponseBody
    public ResponseEntity removeTag(@CurrentUser Account account, @RequestBody TagForm tagForm){
        String title = tagForm.getTagTitle();
        Tag tag = tagRepository.findByTitle(title);
        if(tag == null){
            return ResponseEntity.badRequest().build();
        }

        accountService.removeTag(account,tag);
        return ResponseEntity.ok().build();

    }


}

