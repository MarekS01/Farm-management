package pl.farmmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.farmmanagement.model.FieldEntity;
import pl.farmmanagement.model.UpdateUserDTO;
import pl.farmmanagement.model.User;
import pl.farmmanagement.security.LoggedUserDetails;
import pl.farmmanagement.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class UserController {
    private final UserService userService;


    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping(value = "/signUp")
    public ModelAndView showForm(Model model) {
        ModelAndView modelAndView = new ModelAndView("newUser-form.html");
        model.addAttribute("user", new User());
        return modelAndView;

    }

    @GetMapping
    public ModelAndView loginPage(@RequestParam(required = false) String error, HttpServletRequest httpServletRequest) {
        ModelAndView modelAndView = new ModelAndView("loginPage.html");
        modelAndView.addObject("error", error);
        return modelAndView;
    }

    @PostMapping(value = "/signUp")
    public String processForm(@AuthenticationPrincipal LoggedUserDetails adminDetails,
                              @Valid @ModelAttribute("user") User user,
                              BindingResult result, HttpServletResponse response) {
        if (result.hasErrors()) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return "newUser-form";
        } else {
            userService.add(user);
            return adminDetails == null ? "redirect:/" : "redirect:/admin";
        }
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public String userHomePage(Model model, @AuthenticationPrincipal LoggedUserDetails userDetails,
                               HttpServletRequest request) {
        List<FieldEntity> allUserField = userService.getAllUserFieldById(userDetails.getId());
        model.addAttribute("fields", allUserField);
        request.getSession().removeAttribute("updateMessage");
        return "userHomePage";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("userUpdate")
    public String userCockpit(@AuthenticationPrincipal LoggedUserDetails userDetails, Model model) {
        UpdateUserDTO theUser = userService.getByUserName(userDetails.getUsername());
        model.addAttribute("user", theUser);
        return "userCockpit";
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("userUpdate/givenName")
    public String updateUserGivenName(@ModelAttribute("user") @Valid UpdateUserDTO user,
                                    BindingResult bindingResult,
                                    HttpServletRequest request) {
        if (bindingResult.hasFieldErrors("givenName")) {
            request.getSession().setAttribute("updateMessage","Incorrect new first name");
        } else {
            request.getSession().setAttribute("updateMessage","The first name was changed successfully");
            userService.updateUserGivenName(user);
        }
            return "redirect:/userUpdate";
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("userUpdate/surname")
    public String updateUserSurname(@ModelAttribute("user") @Valid UpdateUserDTO user,
                                    BindingResult bindingResult,
                                    HttpServletRequest request) {
        if (bindingResult.hasFieldErrors("surname")) {
            request.getSession().setAttribute("updateMessage","Incorrect new last name");
        } else {
            request.getSession().setAttribute("updateMessage","The last name was changed successfully");
            userService.updateUserSurname(user);
        }
            return "redirect:/userUpdate";
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("userUpdate/email")
    public String updateEmail(@ModelAttribute("user") @Valid UpdateUserDTO user,
                                    BindingResult bindingResult,
                                    HttpServletRequest request) {
        if (bindingResult.hasFieldErrors("eMail")) {
            request.getSession().setAttribute("updateMessage","Incorrect new email");
        } else {
            request.getSession().setAttribute("updateMessage","The email was changed successfully");
            userService.updateUserEmail(user);
        }
        return "redirect:/userUpdate";
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("userUpdate/password")
    public String updatePassword(@ModelAttribute("user") @Valid UpdateUserDTO user,
                              BindingResult bindingResult,
                              HttpServletRequest request) {
        if (bindingResult.hasGlobalErrors()) {
            request.getSession().setAttribute("updateMessage","Password change failed");
        } else {
            request.getSession().setAttribute("updateMessage","The password was changed successfully");
            userService.updateUserPassword(user);
        }
        return "redirect:/userUpdate";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ModelAndView adminPage() {
        List<User> userList = userService.findAllUsers();
        ModelAndView modelAndView = new ModelAndView("adminPage.html");
        modelAndView.addObject("users", userList);
        return modelAndView;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/delete")
    public String deleteField(@RequestParam("id") Long id) {
        userService.delete(id);
        return "redirect:/admin";
    }

}
