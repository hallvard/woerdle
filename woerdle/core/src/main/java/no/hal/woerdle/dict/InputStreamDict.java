package no.hal.woerdle.dict;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Function;

/**
 * Dict that read its words from an InputStream.
 */
public abstract class InputStreamDict implements Dict {

  public abstract InputStream getInputStream(); 

  private int currentWordLength = 0;
  private String[] wordCache = null;

  private void ensureCache(int wordLength) throws IOException {
    if (currentWordLength != wordLength) {
      wordCache = null;
      try (InputStream inputStream = getInputStream();
          BufferedReader reader = new BufferedReader(
              new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
        wordCache = reader.lines()
          .filter(s -> s.length() == wordLength)
          .map(String::toLowerCase)
          .toList().toArray(new String[0]);
        Arrays.sort(wordCache);
      }
    }
  }

  @Override
  public boolean hasWord(String word) {
    try {
      ensureCache(word.length());
      return Arrays.binarySearch(wordCache, word.toLowerCase()) >= 0;
    } catch (IOException ioex) {
      return false;
    }
  }

  @Override
  public String getRandomWord(int wordLength, Function<Integer, Integer> random) {
    try {
      ensureCache(wordLength);
      if (wordCache != null) {
        int pos = random.apply(wordCache.length);
        return wordCache[pos].toLowerCase();
      }
    } catch (IOException e) {
      return null;
    }
    return null;
  }  
}
