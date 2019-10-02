package pl.farmmanagement.helper;

import lombok.RequiredArgsConstructor;
import pl.farmmanagement.model.UpdateUserDTO;
import pl.farmmanagement.model.UserEntity;
import pl.farmmanagement.repository.UserRepository;
import pl.farmmanagement.security.SecurityConfig;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class UpdatePasswordValidator implements ConstraintValidator<UpdatePasswordValid, UpdateUserDTO> {

    private final UserRepository userRepository;
    private final SecurityConfig securityConfig;

    public void initialize(UpdatePasswordValid constraint) {
    }

    public boolean isValid(UpdateUserDTO user, ConstraintValidatorContext context) {
        if (user.getOldPassword() == null) {
            return false;
        }
        UserEntity theUser = userRepository.findById(user.getId())
                .orElseThrow(RuntimeException::new);
        Pattern pattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
        return securityConfig.passwordEncoder().matches(user.getOldPassword(), theUser.getPassword())
                && pattern.matcher(user.getPassword()).matches()
                && user.getPassword().equals(user.getRePassword());
    }
}
