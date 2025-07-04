package io.ib67.kiwi.event;

import io.ib67.kiwi.TypeToken;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestTypeTokenSet {

    @Test
    void testPutAndGet() {
        TypeTokenSet set = new TypeTokenSet(16);
        TypeToken<String> stringToken = new TypeToken<String>() {};
        TypeToken<Integer> intToken = new TypeToken<Integer>() {};

        set.put(stringToken, true);
        set.put(intToken, false);

        assertTrue(set.get(stringToken));
        assertFalse(set.get(intToken));
    }

    @Test
    void testContainsKey() {
        TypeTokenSet set = new TypeTokenSet(16);
        TypeToken<String> stringToken = new TypeToken<String>() {};
        TypeToken<Integer> intToken = new TypeToken<Integer>() {};

        set.put(stringToken, true);

        assertTrue(set.containsKey(stringToken));
        assertFalse(set.containsKey(intToken));
    }

    @Test
    void testResize() {
        TypeTokenSet set = new TypeTokenSet(2);
        TypeToken<String> stringToken = new TypeToken<String>() {};
        TypeToken<Integer> intToken = new TypeToken<Integer>() {};
        TypeToken<Double> doubleToken = new TypeToken<Double>() {};

        set.put(stringToken, true);
        set.put(intToken, false);
        set.put(doubleToken, true);

        assertEquals(3, set.size());
        assertTrue(set.get(stringToken));
        assertFalse(set.get(intToken));
        assertTrue(set.get(doubleToken));
    }

    @Test
    void testSize() {
        TypeTokenSet set = new TypeTokenSet(16);
        TypeToken<String> stringToken = new TypeToken<>() {
        };
        TypeToken<Integer> intToken = new TypeToken<>() {
        };

        assertEquals(0, set.size());
        set.put(stringToken, true);
        assertEquals(1, set.size());
        set.put(intToken, false);
        assertEquals(2, set.size());
    }
}
