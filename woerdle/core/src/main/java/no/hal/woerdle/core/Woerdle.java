package no.hal.woerdle.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The game.
 */
public class Woerdle {

  private final WoerdleConfig config;

  public Woerdle(WoerdleConfig config) {
    this.config = config;
  }

  private char[] solution;
  private List<char[]> attempts;
  private List<CharSlotKind[]> slotKinds;

  public boolean isStarted() {
    return solution != null;
  }

  /**
   * Gets the number of attempts.
   *
   * @return the number of attempts
   * @throws IllegalStateException if the game hasn't started
   */
  public int getAttemptCount() {
    if (! isStarted()) {
      throw new IllegalStateException("Not started");
    }
    return attempts.size();
  }

  public String getAttempt(int num) {
    return new String(attempts.get(num));
  }

  /**
   * Tells if the game is finished.
   *
   * @return if the game is startet and finised
   */
  public boolean isFinished() {
    return isStarted()
      && (getAttemptCount() == config.maxAttemptCount()
        || (attempts.size() > 0 && Arrays.equals(solution, attempts.get(attempts.size() - 1))));
  }

  /**
   * Tells if the game is not (yet) finished.
   *
   * @return if the game is started, but not finised
   */
  public boolean isPlaying() {
    return isStarted() && (! isFinished());
  }

  /**
   * Tells if the game is won.
   *
   * @return if the game is won (assuming it is finished)
   * @throws IllegalStateException if the game isn't finished
   */
  public boolean isWon() {
    if (! isFinished()) {
      throw new IllegalStateException("Not finished");
    }
    return Arrays.equals(solution, attempts.get(attempts.size() - 1));
  }

  /**
   * Tells if the game is lost.
   *
   * @return if the game is lost (assuming it is finished)
   * @throws IllegalStateException if the game isn't finished
   */
  public boolean isLost() {
    if (! isFinished()) {
      throw new IllegalStateException("Not finished");
    }
    return ! isWon();
  }

  /**
   * Start the game with the provided solution.
   *
   * @param solution the solution
   * @throws IllegalStateException if it already has started
   * @throws IllegalArgumentException if the solution doesn't have the appropriate length
   */
  public void start(String solution) {
    if (isStarted()) {
      throw new IllegalStateException("Already started");
    }  
    if (solution.length() != config.wordLength()) {
      throw new IllegalArgumentException("Wrong wordlength, should be "
         + config.wordLength() + " but was " + solution.length());
    }  
    this.solution = solution.toUpperCase().toCharArray();
    this.attempts = new ArrayList<>();
    this.slotKinds = new ArrayList<>();
  }  

  /**
   * Attempts the provded String.
   *
   * @param attempt the attemtpt
   * @return the classification of the letters
   */
  public CharSlotKind[] attempt(String attempt) {
    if (! isStarted()) {
      throw new IllegalStateException("Not started");
    }
    if (isFinished()) {
      throw new IllegalStateException("Already finished");
    }
    if (attempt.length() != config.wordLength()) {
      throw new IllegalArgumentException("Wrong wordlength, should be "
        + config.wordLength() + " but was " + attempt.length());
    }
    if (config.dict() != null && (! config.dict().hasWord(attempt))) {
      throw new IllegalStateException(attempt + " is not in dictionary");
    }
    return attempt(attempt.toUpperCase().toCharArray());
  }

  private CharSlotKind[] attempt(char[] attempt) {
    attempts.add(attempt);
    var slotKindArray = classifyCharSlots(attempt, solution);
    this.slotKinds.add(slotKindArray);
    return slotKindArray;
  }

  /**
   * Gets the classification of the specified character in the given attempts.
   *
   * @param attemptNum the attempt
   * @param charNum the character within that attempt
   * @return the classification
   */
  public CharSlotKind getCharSlotKind(int attemptNum, int charNum) {
    if (attemptNum >= getAttemptCount()) {
      throw new IllegalArgumentException("Attempt num (" + attemptNum
        + ") must be less than attempt count (" + getAttemptCount() + ")");
    }
    if (charNum >= config.wordLength()) {
      throw new IllegalArgumentException("Char num (" + charNum
        + ") must be less than word length (" + config.wordLength() + ")");
    }
    return slotKinds.get(attemptNum)[charNum];
  }

  /**
   * Classification of characters in an attempts.
   */
  public enum CharSlotKind {
    CORRECT, MISPLACED, WRONG
  }

  static CharSlotKind[] classifyCharSlots(char[] attempt, char[] solution) {
    if (attempt.length != solution.length) {
      throw new IllegalArgumentException("Try and solution must be of same length: "
        + new String(attempt) + " vs. " + new String(solution));
    }
    CharSlotKind[] kinds = new CharSlotKind[attempt.length];
    Map<Character, Integer> charCounts = new HashMap<>();
    // init kinds and counts
    for (int i = 0; i < attempt.length; i++) {
      char c = solution[i];
      if (attempt[i] == c) {
        kinds[i] = CharSlotKind.CORRECT;
      } else {
        kinds[i] = CharSlotKind.WRONG;
        charCounts.put(c, charCounts.computeIfAbsent(c, ignore -> 0) + 1);
      }
    }
    for (int i = 0; i < attempt.length; i++) {
      if (kinds[i] != CharSlotKind.CORRECT) {
        char c = attempt[i];
        if (charCounts.containsKey(c)) {
          int count = charCounts.get(c);
          if (count > 0) {
            kinds[i] = CharSlotKind.MISPLACED;
            charCounts.put(c, count - 1);
          }
        }
      }
    }
    return kinds;
  }
}
