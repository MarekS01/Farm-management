package pl.farmmanagement.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.farmmanagement.repository.FieldRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.security.Principal;

@RequiredArgsConstructor
public class UniqueFieldNameValidator implements ConstraintValidator<UniqueFieldName, String> {

    private final FieldRepository fieldRepository;
    private Principal principal;

    public void initialize(UniqueFieldName constraint) {
    }

    public boolean isValid(String fieldName, ConstraintValidatorContext context) {
        principal = SecurityContextHolder.getContext().getAuthentication();
        if (fieldName != null) {
            fieldName = fieldName.toLowerCase();
        }
        return !fieldRepository.findByNameIgnoreCaseAndUserName(fieldName, principal.getName()).isPresent();
    }
}
