package mate.academy.app.lib;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import mate.academy.app.lib.annotations.FieldMatch;
import org.springframework.beans.BeanWrapperImpl;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstFieldName;
    private String secondFieldName;
    private String message;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        this.firstFieldName = constraintAnnotation.first();
        this.secondFieldName = constraintAnnotation.second();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        Object firstField = new BeanWrapperImpl(obj).getPropertyValue(this.firstFieldName);
        Object secondField = new BeanWrapperImpl(obj).getPropertyValue(this.secondFieldName);
        boolean isValid = firstField != null && firstField.equals(secondField);
        if (!isValid) {
            context.disableDefaultConstraintViolation();

            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(secondFieldName)
                    .addConstraintViolation();
        }
        return isValid;
    }
}
