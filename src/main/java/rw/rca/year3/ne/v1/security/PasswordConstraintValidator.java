package rw.rca.year3.ne.v1.security;


import com.google.common.base.Joiner;
import org.passay.*;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword,String> {
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {

        PasswordValidator validator = new PasswordValidator(
                Arrays.asList(new LengthRule(5, 30),
                        new UppercaseCharacterRule(1),
                        new LowercaseCharacterRule(1),
                        new SpecialCharacterRule(1),
                        new DigitCharacterRule(1),
                        new WhitespaceRule()));

        RuleResult result = validator.validate(new PasswordData(password));

        if (result.isValid()) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(Joiner.on(",").join(validator.getMessages(result)))
                .addConstraintViolation();
        return false;
    }
}
