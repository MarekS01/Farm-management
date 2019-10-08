package pl.farmmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import pl.farmmanagement.model.FieldDTO;
import pl.farmmanagement.service.FieldService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class FieldController {

    private final FieldService fieldService;

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/newField")
    public String showFormForAddField(Model theModel, HttpServletRequest request) {
        FieldDTO newField = new FieldDTO();
        request.getSession().setAttribute("updateOrAdd", "Add");
        theModel.addAttribute("newField", newField);
        return "newField-form";
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/newField")
    public String saveField(Principal loggedUserDetails,
                            @ModelAttribute("newField") @Valid FieldDTO field,
                            BindingResult bindingResult,
                            HttpServletResponse response) {

        if (bindingResult.hasErrors()) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return "newField-form";
        } else {
            fieldService.addField(loggedUserDetails.getName(), field);
            return "redirect:/user";
        }
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/delete")
    public String deleteField(@RequestParam("id") Long id) {
        fieldService.deleteField(id);
        return "redirect:/user";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/updateField")
    public String updateField(@RequestParam("id") Long id, Model model, HttpServletRequest request) {
        FieldDTO updatedField = fieldService.findFieldById(id);
        request.getSession().setAttribute("updateOrAdd", "Update");
        model.addAttribute("newField", updatedField);
        return "newField-form";
    }

}
