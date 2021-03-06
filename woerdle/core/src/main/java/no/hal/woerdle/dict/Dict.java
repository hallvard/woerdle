package no.hal.woerdle.dict;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

/**
 * Dictionary that provides words for the game.
 */
public interface Dict {
  /**
   * Tells of the word exists in this dictionary.
   *
   * @param word the word to lookup
   * @return true if the word exists, otherwise false
   */
  boolean hasWord(String word);

  /**
   * Returns a random word with the specific charCount.
   *
   * @param charCount the desired word length
   * @param random a function that returns an index betwen 0 and upto but not including its argument
   * @return the random word
   */
  String getRandomWord(int charCount, Function<Integer, Integer> random);

  /**
   * Creates a dictionary with the provided words.
   *
   * @param words words separated by whitespace
   * @return the new dictionary
   */
  public static Dict of(String words) {
    return of(words, "\\s+");
  }

  /**
   * Creates a dictionary with the provided words.
   *
   * @param words words separated by separator (regexp)
   * @param separator the separator
   * @return the new dictionary
   */
  public static Dict of(String words, String separator) {
    return of(Arrays.asList(words.split(separator)));
  }

  /**
   * Creates a dictionary with the provided words.
   *
   * @param words the words
   * @return the new dictionary
   */
  public static Dict of(Collection<String> words) {
    return new InputStreamDict() {
      @Override
      public InputStream getInputStream() {
        return new ByteArrayInputStream(String.join("\n", words).getBytes(StandardCharsets.UTF_8));
      }
    };
  }
}