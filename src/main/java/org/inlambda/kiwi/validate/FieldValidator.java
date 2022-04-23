package org.inlambda.kiwi.validate;

public interface FieldValidator {
    boolean validate(Object object, String expr);
}
