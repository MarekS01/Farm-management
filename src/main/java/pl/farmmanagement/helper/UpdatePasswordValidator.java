package pl.farmmanagement.helper;

import lombok.RequiredArgsConstructor;
import pl.farmmanagement.model.UpdateUserDTO;
import pl.farmmanagement.model.User;
import pl.farmmanagement.security.SecurityConfig;
import pl.farmmanagement.service.UserService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class UpdatePasswordValidator implements ConstraintValidator<UpdatePasswordValid, UpdateUserDTO> {

    private final SecurityConfig securityConfig;
    private final UserService userService;

    public void initialize(UpdatePasswordValid constraint) {
    }

    public boolean isValid(UpdateUserDTO user, ConstraintValidatorContext context) {
        if (user.getOldPassword() == null) {
            return false;
        }
        User theUser = userService.findByUserId(user.getId())
                .orElseThrow(RuntimeException::new);
        Pattern pattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
        return securityConfig.passwordEncoder().matches(user.getOldPassword(), theUser.getPassword())
                && pattern.matcher(user.getPassword()).matches()
                && user.getPassword().equals(user.getRePassword());
    }
}
