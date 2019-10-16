package pl.farmmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.farmmanagement.model.Field;
import pl.farmmanagement.model.dto.UserDTO;
import pl.farmmanagement.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.Principal;
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

    @GetMapping
    public ModelAndView loginPage(@RequestParam(required = false) String error) {
        ModelAndView modelAndView = new ModelAndView("loginPage.html");
        modelAndView.addObject("error", error);
        return modelAndView;
    }

    @GetMapping(value = "/signUp")
    public ModelAndView showForm(Model model) {
        ModelAndView modelAndView = new ModelAndView("newUser-form.html");
        model.addAttribute("user", new UserDTO());
        return modelAndView;
    }

    @PostMapping(value = "/signUp")
    public String processForm(Principal adminDetails,
                              @Valid @ModelAttribute("user") UserDTO user,
                              BindingResult result,
                              HttpServletResponse response) {
        if (result.hasErrors()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "newUser-form";
        } else {
            userService.add(user);
            return adminDetails != null && "admin".equalsIgnoreCase(adminDetails.getName()) ?
                    "redirect:/admin" : "redirect:/";
        }
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public String userHomePage(Model model,
                               Principal principal,
                               HttpServletRequest request) {
        List<Field> allUserField = userService.findAllUserFieldByUserName(principal.getName());
        model.addAttribute("fields", allUserField);
        request.getSession().removeAttribute("updateMessage");
        return "userHomePage";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ModelAndView adminPage() {
        List<UserDTO> userList = userService.findAllUsers();
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
