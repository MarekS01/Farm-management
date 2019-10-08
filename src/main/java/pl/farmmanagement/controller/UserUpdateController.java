package pl.farmmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import pl.farmmanagement.model.UpdateUserDTO;
import pl.farmmanagement.service.UserUpdateService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/userUpdate")
@PreAuthorize("hasRole('USER')")
public class UserUpdateController {

    private final UserUpdateService userUpdateService;

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping
    public String userCockpit(Principal principal, Model model) {
        UpdateUserDTO theUser = userUpdateService.findByUserName(principal.getName());
        model.addAttribute("user", theUser);
        return "userCockpit";
    }

    @PostMapping("givenName")
    public String updateUserGivenName(@ModelAttribute("user") @Valid UpdateUserDTO user,
                                      BindingResult bindingResult,
                                      HttpServletRequest request) {
        if (bindingResult.hasFieldErrors("givenName")) {
            request.getSession().setAttribute("updateMessage", "Incorrect new first name");
        } else {
            request.getSession().setAttribute("updateMessage", "The first name was changed successfully");
            userUpdateService.updateUserGivenName(user);
        }
        return "redirect:/userUpdate";
    }

    @PostMapping("surname")
    public String updateUserSurname(@ModelAttribute("user") @Valid UpdateUserDTO user,
                                    BindingResult bindingResult,
                                    HttpServletRequest request) {
        if (bindingResult.hasFieldErrors("surname")) {
            request.getSession().setAttribute("updateMessage", "Incorrect new last name");
        } else {
            request.getSession().setAttribute("updateMessage", "The last name was changed successfully");
            userUpdateService.updateUserSurname(user);
        }
        return "redirect:/userUpdate";
    }

    @PostMapping("email")
    public String updateEmail(@ModelAttribute("user") @Valid UpdateUserDTO user,
                              BindingResult bindingResult,
                              HttpServletRequest request) {
        if (bindingResult.hasFieldErrors("eMail")) {
            request.getSession().setAttribute("updateMessage", "Incorrect new email");
        } else {
            request.getSession().setAttribute("updateMessage", "The email was changed successfully");
            userUpdateService.updateUserEmail(user);
        }
        return "redirect:/userUpdate";
    }

    @PostMapping("password")
    public String updatePassword(@ModelAttribute("user") @Valid UpdateUserDTO user,
                                 BindingResult bindingResult,
                                 HttpServletRequest request) {
        if (bindingResult.hasGlobalErrors()) {
            request.getSession().setAttribute("updateMessage", "Password change failed");
        } else {
            request.getSession().setAttribute("updateMessage", "The password was changed successfully");
            userUpdateService.updateUserPassword(user);
        }
        return "redirect:/userUpdate";
    }
}
