package no.hal.woerdle.dict;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ResourceDictTest {
    
    private static Dict dict;

    @BeforeAll
    public static void initDict() {
        dict = new ResourceDict("/no/hal/woerdle/dict/NSF2021.txt");
    }

    @Test
    public void testHasWord() {
        assertTrue(dict.hasWord("onomatopoetikon"));
        assertFalse(dict.hasWord("xyzæøå"));
        assertTrue(dict.hasWord("ord"));
        assertFalse(dict.hasWord("xyz"));
        assertTrue(dict.hasWord("abc"));
    }
}
