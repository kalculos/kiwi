package org.inlambda.kiwi.validate;

import org.inlambda.kiwi.reflection.AccessibleClass;
import org.inlambda.kiwi.reflection.AccessibleField;
import org.inlambda.kiwi.tuple.Pair;
import org.inlambda.kiwi.tuple.Triple;

import java.util.*;

import static java.util.Objects.requireNonNull;

public class SimpleValidator implements Validator {
    private final Map<Class<? extends FieldValidator>, FieldValidator> validatorMap = new WeakHashMap<>();

    private final Map<Class<?>, AccessibleClass<?>> cachedClasses = new WeakHashMap<>();

    @Override
    public void registerFieldValidator(FieldValidator validator) {
        requireNonNull(validator);
        validatorMap.put(validator.getClass(), validator);
    }

    @Override
    public boolean validateObject(Object o) {
        var clazz = cachedClasses.computeIfAbsent(o.getClass(), z -> AccessibleClass.of(z).fillInFields());
        for (AccessibleField<?> field : clazz.fields()) {
            var f = field.reflect();
            if (!f.isAnnotationPresent(Validate.class)) {
                continue;
            }
            var validate = f.getAnnotation(Validate.class);
            var validator = validatorMap.get(validate.validator());
            if (validator == null) {
                throw new IllegalStateException("No validator found for " + validate.validator());
            }
            if (!validator.validate(field.get(o), validate.value())) {
                return false;
            }
        }
        return true;
    }
}
