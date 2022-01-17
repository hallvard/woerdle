package no.hal.woerdle.core;

import static no.hal.woerdle.core.Woerdle.CharSlotKind.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.hal.woerdle.core.Woerdle.CharSlotKind;

public class WoerdleTest {
  
  private Woerdle gjetord;

  @BeforeEach
  public void setup() {
    gjetord = new Woerdle(new WoerdleConfig(5, 3, null));
  }

  @Test
  public void testStart() {
    assertThrows(IllegalArgumentException.class, () -> gjetord.start("anything"));
    gjetord.start("12345");
    assertThrows(IllegalStateException.class, () -> gjetord.start("anything"));
  }

  @Test
  public void testIsStarted() {
    assertFalse(gjetord.isStarted());
    gjetord.start("12345");
    assertTrue(gjetord.isStarted());
  }

  @Test
  public void testIsFinished_lost() {
    gjetord.start("12345");
    assertFalse(gjetord.isFinished());
    gjetord.attempt("adfgh");
    gjetord.attempt("adfgh");
    gjetord.attempt("adfgh");
    assertTrue(gjetord.isFinished());
  }

  @Test
  public void testIsFinished_won1() {
    gjetord.start("12345");
    assertFalse(gjetord.isFinished());
    gjetord.attempt("adfgh");
    gjetord.attempt("12345");
    assertTrue(gjetord.isFinished());
  }

  @Test
  public void testIsFinished_won3() {
    gjetord.start("12345");
    assertFalse(gjetord.isFinished());
    gjetord.attempt("adfgh");
    gjetord.attempt("adfgh");
    gjetord.attempt("12345");
    assertTrue(gjetord.isFinished());
  }

  @Test
  public void testIsLost() {
    gjetord.start("12345");
    assertThrows(IllegalStateException.class, () -> gjetord.isLost());
    gjetord.attempt("adfgh");
    gjetord.attempt("adfgh");
    gjetord.attempt("adfgh");
    assertTrue(gjetord.isLost());
    assertFalse(gjetord.isWon());
  }

  @Test
  public void testIsWon() {
    gjetord.start("12345");
    assertThrows(IllegalStateException.class, () -> gjetord.isWon());
    gjetord.attempt("adfgh");
    gjetord.attempt("adfgh");
    gjetord.attempt("12345");
    assertTrue(gjetord.isWon());
    assertFalse(gjetord.isLost());
  }

  private void assertSlotKinds(List<CharSlotKind> expected, CharSlotKind[] actual) {
    assertEquals(new ArrayList<>(expected), Arrays.asList(actual));
  }

  @Test
  public void testAttempt() {
    assertThrows(IllegalStateException.class, () -> gjetord.attempt("adfgh"));
    gjetord.start("abcde");
    assertThrows(IllegalArgumentException.class, () -> gjetord.attempt("anything"));
    assertSlotKinds(List.of(CORRECT, MISPLACED, WRONG, WRONG, WRONG), gjetord.attempt("adfgh"));
    gjetord.attempt("adfgh");
    gjetord.attempt("adfgh");
    assertThrows(IllegalStateException.class, () -> gjetord.attempt("adfgh"));
  }

  @Test
  public void testclassifyCharSlots() {
    assertSlotKinds(List.of(MISPLACED, CORRECT, MISPLACED, MISPLACED, MISPLACED), Woerdle.classifyCharSlots("AADBC".toCharArray(), "BAACD".toCharArray()));
    assertSlotKinds(List.of(WRONG, CORRECT, CORRECT, MISPLACED, MISPLACED), Woerdle.classifyCharSlots("AAABC".toCharArray(), "BAACD".toCharArray()));
  }
}
