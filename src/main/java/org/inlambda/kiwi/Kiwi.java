package org.inlambda.kiwi;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.inlambda.kiwi.lazy.LazyFunction;
import org.inlambda.kiwi.lazy.LazySupplier;
import org.inlambda.kiwi.range.IntRange;
import org.inlambda.kiwi.validate.SimpleValidator;
import org.inlambda.kiwi.validate.Validator;
import org.inlambda.kiwi.validate.validator.ScriptValidator;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@UtilityClass
public class Kiwi {
    public static <T> Optional<T> runAny(AnySupplier<T> supplier){
        try{
            var result = supplier.get();
            return Optional.ofNullable(result);
        } catch (Throwable e) {
            return Optional.empty();
        }
    }
    public static <V> LazySupplier<V> byLazy(Supplier<V> supplier){
        return LazySupplier.by(supplier);
    }

    public static <K,V> LazyFunction<K,V> byLazy(Function<K,V> function){
        return LazyFunction.by(function);
    }

    public static Validator createBeanValidator(){
        return createBeanValidator(createScriptEngine(),false);
    }

    private static ScriptEngine createScriptEngine() {
        return new ScriptEngineManager().getEngineByExtension("js");
    }

    @SneakyThrows
    public static Validator createBeanValidator(ScriptEngine se, boolean ignoreScriptErrors){
        //validator.registerFieldValidator(new ScriptValidator(se,ignoreScriptErrors));
        return new SimpleValidator();
    }
}
