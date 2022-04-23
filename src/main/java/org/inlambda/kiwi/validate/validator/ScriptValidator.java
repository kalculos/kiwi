package org.inlambda.kiwi.validate.validator;

import lombok.RequiredArgsConstructor;
import org.inlambda.kiwi.validate.FieldValidator;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

@RequiredArgsConstructor
public class ScriptValidator implements FieldValidator {
    private final ScriptEngine scriptEngine;
    private final boolean tolerateScriptExceptions;
    @Override
    public boolean validate(Object object, String expr) {
        try {
            scriptEngine.put("i",object);
            scriptEngine.eval(expr);
            return (boolean) scriptEngine.eval(expr);
        } catch (ScriptException | ClassCastException e) {
            if(!tolerateScriptExceptions) e.printStackTrace();
            return false;
        }
    }
}
