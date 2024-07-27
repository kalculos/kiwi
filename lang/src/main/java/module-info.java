module kiwi.core {
    exports io.ib67.kiwi.lazy;
    exports io.ib67.kiwi;
    exports io.ib67.kiwi.reflection;
    exports io.ib67.kiwi.tuple;
    exports io.ib67.kiwi.lock;
    exports io.ib67.kiwi.future;
    exports io.ib67.kiwi.exception;
    requires org.jetbrains.annotations;
    requires lombok;
    requires jdk.unsupported;
}