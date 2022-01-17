package no.hal.woerdle.core;

import no.hal.woerdle.dict.Dict;

/**
 * Configuration options for Woerdle games.
 */
public class WoerdleConfig {
    
  private final int wordLength;
  private final int maxAttemptCount;
  private final Dict dict;

  /**
   * Initialises the configuration.
   *
   * @param wordLength the word length
   * @param maxAttemptCount the maxumum number of attempts
   * @param dict optional dictionary
   */
  public WoerdleConfig(int wordLength, int maxAttemptCount, Dict dict) {
    if (wordLength <= 0) {
      throw new IllegalArgumentException("word length must be positive");
    }
    if (maxAttemptCount <= 0) {
      throw new IllegalArgumentException("max attempt count must be positive");
    }
    this.wordLength = wordLength;
    this.maxAttemptCount = maxAttemptCount;
    this.dict = dict;
  }

  public WoerdleConfig() {
    this(5, 6, null);
  }

  public int wordLength() {
    return wordLength;
  }

  public int maxAttemptCount() {
    return maxAttemptCount;
  }

  public Dict dict() {
    return dict;
  }
}
