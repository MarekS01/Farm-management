package pl.farmmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import pl.farmmanagement.model.FieldOperation;
import pl.farmmanagement.service.FieldOperationService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user/operations")
@PreAuthorize("hasRole('USER')")
public class FieldOperationController {

    private final FieldOperationService fieldOperationService;

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping
    public String operations(@RequestParam("id") Long id,
                             @RequestParam("fieldName") String fieldName,
                             Model model,
                             HttpServletRequest request) {

        request.getSession().setAttribute("fieldId", id);
        request.getSession().setAttribute("fieldName", fieldName);
        List<FieldOperation> allFieldOperations =
                fieldOperationService.findAllOperationsByField(id);
        model.addAttribute("operations", allFieldOperations);
        return "operations";
    }

    @GetMapping(value = "/new")
    public String newOperation(Model model, HttpServletRequest request) {
        FieldOperation fieldOperation = new FieldOperation();
        request.getSession().setAttribute("addOrUpdateOperation", "Add");
        model.addAttribute("operation", fieldOperation);
        return "operation-form";
    }

    @PostMapping(value = "/new")
    public String processForm(@ModelAttribute("operation") @Valid FieldOperation newOperation,
                              BindingResult result,
                              HttpServletRequest request,
                              HttpServletResponse response) {

        if (result.hasErrors()) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return "operation-form";
        } else {
            Long fieldId = (Long) request.getSession().getAttribute("fieldId");
            String fieldName = (String) request.getSession().getAttribute("fieldName");
            fieldOperationService.addFieldOperation(fieldId, newOperation);
            return String.format("redirect:/user/operations?id=%d&fieldName=%s",
                    fieldId, fieldName);
        }
    }

    @GetMapping("/updateOperation")
    public String updateOperations(@RequestParam("id") Long id, Model model, HttpServletRequest request) {
        Optional<FieldOperation> theOperation = fieldOperationService.findOperationById(id);
        FieldOperation fieldOperation = theOperation.orElseGet(FieldOperation::new);
        request.getSession().setAttribute("addOrUpdateOperation", "Update");
        model.addAttribute("operation", fieldOperation);
        return "operation-form";
    }


    @GetMapping("/delete")
    public String deleteById(@RequestParam("id") Long id,
                             HttpServletRequest request) {
        Long fieldId = (Long) request.getSession().getAttribute("fieldId");
        String fieldName = (String) request.getSession().getAttribute("fieldName");
        fieldOperationService.deleteById(id);
        return String.format("redirect:/user/operations?id=%d&fieldName=%s", fieldId, fieldName);
    }

    @GetMapping("/doneTask")
    public String doneTask(@RequestParam("id") Long operationId, HttpServletRequest request) {
        Long fieldId = (Long) request.getSession().getAttribute("fieldId");
        String fieldName = (String) request.getSession().getAttribute("fieldName");
        fieldOperationService.doneTask(fieldId, operationId);
        return String.format("redirect:/user/operations?id=%d&fieldName=%s", fieldId, fieldName);
    }
}
