package no.hal.woerdle.dict;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import no.hal.woerdle.dict.Dict;
import no.hal.woerdle.dict.ResourceDicts;

public class ResourceDictTest {
    
    private static Dict dict;

    @BeforeAll
    public static void initDict() {
        dict = ResourceDicts.NB.getDict();
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
