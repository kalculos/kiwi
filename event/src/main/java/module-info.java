module kiwi.event {
    requires kiwi.core;
    requires static lombok;
    requires org.jetbrains.annotations;
    requires org.objectweb.asm;
    exports io.ib67.kiwi.event;
    exports io.ib67.kiwi.event.api;
    exports io.ib67.kiwi.event.api.annotation;
    exports io.ib67.kiwi.event.util;
}