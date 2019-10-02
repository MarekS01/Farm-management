package pl.farmmanagement.helper;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UpdatePasswordValidator.class)
public @interface UpdatePasswordValid {

    String message() default "Password change failed";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
