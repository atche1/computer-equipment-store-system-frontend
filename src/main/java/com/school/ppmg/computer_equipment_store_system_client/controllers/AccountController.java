package com.school.ppmg.computer_equipment_store_system_client.controllers;

import com.school.ppmg.computer_equipment_store_system_client.clients.UserClient;
import com.school.ppmg.computer_equipment_store_system_client.dtos.user.ChangePasswordRequest;
import com.school.ppmg.computer_equipment_store_system_client.dtos.user.MyAccountResponse;
import com.school.ppmg.computer_equipment_store_system_client.dtos.user.UpdateMyAccountRequest;
import com.school.ppmg.computer_equipment_store_system_client.exceptions.BackendException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final UserClient userClient;

    @GetMapping("/my-account")
    public String myAccount(HttpSession session, Model model) {
        if (session.getAttribute(AuthController.SESSION_ACCESS_TOKEN) == null) {
            return "redirect:/login";
        }

        try {
            MyAccountResponse account = userClient.getMyAccount();

            boolean isAdmin = "ADMIN".equals(account.role());
            model.addAttribute("account", account);
            model.addAttribute("isAdmin", isAdmin);

            return "account/my-account";
        } catch (Exception ex) {
            model.addAttribute("error", "Unable to load account information.");
            return "account/my-account";
        }
    }
    @GetMapping("/my-account/edit")
    public String editMyAccount(HttpSession session, Model model) {
        if (session.getAttribute(AuthController.SESSION_ACCESS_TOKEN) == null) {
            return "redirect:/login";
        }

        try {
            MyAccountResponse account = userClient.getMyAccount();

            UpdateMyAccountRequest form = new UpdateMyAccountRequest();
            form.setFirstName(account.firstName());
            form.setLastName(account.lastName());
            form.setPhone(account.phone());

            model.addAttribute("account", account);
            model.addAttribute("form", form);

            return "account/edit-profile";
        } catch (Exception ex) {
            model.addAttribute("error", "Unable to load account information.");
            return "account/my-account";
        }
    }

    @PostMapping("/my-account/edit")
    public String updateMyAccount(@Valid @ModelAttribute("form") UpdateMyAccountRequest form,
                                  BindingResult bindingResult,
                                  HttpSession session,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        if (session.getAttribute(AuthController.SESSION_ACCESS_TOKEN) == null) {
            return "redirect:/login";
        }

        MyAccountResponse account;
        try {
            account = userClient.getMyAccount();
            model.addAttribute("account", account);
        } catch (Exception ex) {
            model.addAttribute("error", "Unable to load account information.");
            return "account/edit-profile";
        }

        if (bindingResult.hasErrors()) {
            return "account/edit-profile";
        }

        try {
            userClient.updateMyAccount(form);
            redirectAttributes.addFlashAttribute("success", "Your profile was updated successfully.");
            return "redirect:/my-account";
        } catch (BackendException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("fieldErrors", ex.getFieldErrors());
            return "account/edit-profile";
        } catch (Exception ex) {
            model.addAttribute("error", "Unable to update profile.");
            return "account/edit-profile";
        }
    }
    @GetMapping("/my-account/change-password")
    public String changePasswordPage(HttpSession session, Model model) {
        if (session.getAttribute(AuthController.SESSION_ACCESS_TOKEN) == null) {
            return "redirect:/login";
        }

        model.addAttribute("form", new ChangePasswordRequest());
        return "account/change-password";
    }
    @PostMapping("/my-account/change-password")
    public String changePassword(
            @Valid @ModelAttribute("form") ChangePasswordRequest form,
            BindingResult bindingResult,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (session.getAttribute(AuthController.SESSION_ACCESS_TOKEN) == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            return "account/change-password";
        }

        try {
            userClient.changePassword(form);
            redirectAttributes.addFlashAttribute("success", "Password changed successfully.");
            return "redirect:/my-account";
        } catch (BackendException ex) {
            model.addAttribute("error", ex.getMessage());
            return "account/change-password";
        } catch (Exception ex) {
            model.addAttribute("error", "Something went wrong.");
            return "account/change-password";
        }
    }
}