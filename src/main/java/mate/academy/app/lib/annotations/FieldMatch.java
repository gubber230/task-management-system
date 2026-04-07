package mate.academy.app.lib.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import mate.academy.app.lib.FieldMatchValidator;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldMatchValidator.class)
public @interface FieldMatch {
    String first();

    String second();

    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
