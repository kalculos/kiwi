/**
 * Dedicated utilities for working easier with Java.
 */
module kiwi.lang {
    exports io.ib67.kiwi;
    exports io.ib67.kiwi.reflection;
    exports io.ib67.kiwi.closure;
    exports io.ib67.kiwi.routine;
    exports io.ib67.kiwi.routine.op;
    exports io.ib67.kiwi.tuple;
    requires org.jetbrains.annotations;
    requires lombok;
    requires jdk.unsupported;
}