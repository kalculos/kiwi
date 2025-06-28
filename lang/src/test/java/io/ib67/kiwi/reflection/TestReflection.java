package io.ib67.kiwi.reflection;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestReflection {
    @Test
    public void testIsPrimitiveOrBox() {
        assertTrue(Reflections.isPrimitiveOrBox(int.class));
        assertTrue(Reflections.isPrimitiveOrBox(Integer.class));
        assertTrue(Reflections.isPrimitiveOrBox(long.class));
        assertTrue(Reflections.isPrimitiveOrBox(Long.class));
        assertTrue(Reflections.isPrimitiveOrBox(double.class));
        assertTrue(Reflections.isPrimitiveOrBox(Double.class));
        assertTrue(Reflections.isPrimitiveOrBox(float.class));
        assertTrue(Reflections.isPrimitiveOrBox(Float.class));
        assertTrue(Reflections.isPrimitiveOrBox(byte.class));
        assertTrue(Reflections.isPrimitiveOrBox(Byte.class));
        assertTrue(Reflections.isPrimitiveOrBox(short.class));
        assertTrue(Reflections.isPrimitiveOrBox(Short.class));
        assertTrue(Reflections.isPrimitiveOrBox(boolean.class));
        assertTrue(Reflections.isPrimitiveOrBox(Boolean.class));
        assertTrue(Reflections.isPrimitiveOrBox(char.class));
        assertTrue(Reflections.isPrimitiveOrBox(Character.class));
        assertFalse(Reflections.isPrimitiveOrBox(String.class));
    }

    @Test
    public void testFieldOf() {
        class TestClass {
            private String testField;
        }
        assertNotNull(Reflections.fieldOf(TestClass.class, "testField"));
        assertThrows(Exception.class, () -> Reflections.fieldOf(TestClass.class, "nonExistentField"));
    }

    @Test
    public void testFieldOfSuper() {
        class Parent {
            private String parentField;
        }
        class Child extends Parent {
        }
        assertNotNull(Reflections.fieldOfSuper(Child.class, "parentField"));
        assertThrows(Exception.class, () -> Reflections.fieldOfSuper(Child.class, "nonExistentField"));
    }

    @Test
    public void testMethodOf() {
        class TestClass {
            private void testMethod() {}
        }
        assertNotNull(Reflections.methodOf(TestClass.class, "testMethod"));
        assertThrows(Exception.class, () -> Reflections.methodOf(TestClass.class, "nonExistentMethod"));
    }

    @Test
    public void testMethodOfSuper() {
        class Parent {
            private void parentMethod() {}
        }
        class Child extends Parent {
        }
        assertNotNull(Reflections.methodOfSuper(Child.class, "parentMethod"));
        assertThrows(Exception.class, () -> Reflections.methodOfSuper(Child.class, "nonExistentMethod"));
    }
}
