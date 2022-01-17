package no.hal.woerdle.dict;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import no.hal.woerdle.dict.Dict;
import no.hal.woerdle.dict.InputStreamDict;

public class InputStreamDictTest {
    
    private static Dict dict;

    @BeforeAll
    public static void initDict() {
        String words = """
        hei
        på
        deg
        """;
        dict = new InputStreamDict() {
            @Override
            public InputStream getInputStream() {
                return new ByteArrayInputStream(words.getBytes(StandardCharsets.UTF_8));
            }
        };
    }

    @Test
    public void testHasWord() {
        assertTrue(dict.hasWord("hei"));
        assertTrue(dict.hasWord("på"));
        assertTrue(dict.hasWord("deg"));
        assertFalse(dict.hasWord("xyz"));
    }

    @Test
    public void testGetRandomWord() {
        int[] wordLengthArg = new int[1];
        Function<Integer, Integer> random = wordLength -> {
            wordLengthArg[0] = wordLength;
            return (int) (Math.random() * wordLength);
        };
        dict.getRandomWord(3, random);
        assertEquals(2, wordLengthArg[0]);
        assertEquals("på", dict.getRandomWord(2, random));
        assertEquals(1, wordLengthArg[0]);
    }
}
