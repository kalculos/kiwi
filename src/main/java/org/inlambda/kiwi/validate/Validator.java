package org.inlambda.kiwi.validate;

import java.util.HashMap;
import java.util.Map;

public interface Validator {
    void registerFieldValidator(FieldValidator validator);
    boolean validateObject(Object o);
}
