package org.inlambda.kiwi.validate;

import org.inlambda.kiwi.validate.validator.ScriptValidator;

import java.lang.annotation.*;

/**
 * A field annotated with this will be checked by {@link Validator}
 * the value is how do we check your value, it depends on the type of your validator.
 * Generally, we take `i` as your field value so that you can write statements in it such as `i != null`.
 */
@Target(ElementType.FIELD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Validate {
    String value() default "i != null";
    Class<? extends FieldValidator> validator() default ScriptValidator.class;
}
