package no.hal.woerdle.fxui;

import com.dlsc.keyboardfx.Keyboard;
import com.dlsc.keyboardfx.KeyboardView;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.function.BiFunction;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Labeled;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javax.xml.bind.JAXBException;
import no.hal.woerdle.core.Woerdle;
import no.hal.woerdle.core.Woerdle.CharSlotKind;
import no.hal.woerdle.core.WoerdleConfig;
import no.hal.woerdle.dict.Dict;
import no.hal.woerdle.dict.ResourceDict;
import no.hal.woerdle.dict.ResourceDicts;
import no.hal.woerdle.fxui.util.DictLabelsSupport;

/**
 * Controller for Woerdle.
 */
public class WoerdleController implements DictLabelsSupport {

  private Woerdle woerdle;

  private WoerdleSettingsController configViewController = new WoerdleSettingsController();
  
  private WoerdleAppSettings settings = new WoerdleAppSettings();

  public WoerdleConfig getConfig() {
    return settings.getConfig();
  }

  private StringBuilder currentAttempt;
  private int currentAttemptPos = 0;

  private Random rand = new Random();

  private BiFunction<Dict, Integer, String> randomWordProvider =
      (dict, wordLength) -> dict.getRandomWord(wordLength, len -> rand.nextInt(len));

  // makes controller more testable
  void setRandomWordProvider(BiFunction<Dict, Integer, String> randomWordProvider) {
    this.randomWordProvider = randomWordProvider;
  }

  @FXML
  private Parent rootPane;

  @FXML private Labeled newGameButton;

  @Override
  public void applyLabels(Properties labels) {
    DictLabelsSupport.applyLabels(labels, newGameButton);
  }

  @FXML
  private GridPane charSlotsGrid;

  @FXML
  private KeyboardView keyboardView;

  private Keyboard keyboard;

  private List<CharSlotNode> charSlotNodes = new ArrayList<>();

  @FXML
  private void initialize() {
    settings.setLang("NB");
    settings.setDictName("NB");
    settings.setConfig(new WoerdleConfig(5, 6, configViewController.getDictForName(settings.getDictName())));

    configViewController.setSettings(settings);

    updateKeyboardAndLabels();
    Platform.runLater(() -> {
      String css = this.getClass().getResource("WoerdleApp.css").toExternalForm();
      charSlotsGrid.getScene().getStylesheets().add(css);
      keyboardView.getStylesheets().add(css);
    });
    charSlotsGrid.setOnKeyTyped(ev -> handleCharSlotKey(ev));
    charSlotsGrid.setOnKeyPressed(ev -> handleCharSlotKey(ev));
    newGame();
  }

  private void updateKeyboardAndLabels() {
    applyLabels(configViewController.getLabelProperties(settings.getLang()));
    try (InputStream input = getClass().getResourceAsStream("keyboard-" + settings.getLang() + ".xml")) {
      if (input != null) {
        keyboard = keyboardView.loadKeyboard(input);
        applyKeyboard();
      }
    } catch (IOException | JAXBException ex) {
      // ignore
    }
  }

  private Parent configRoot = null;

  @FXML
  private void handleSettingsAction() {
    if (configRoot == null) {
      configViewController.setBackCallback(appliedSettings -> {
        if (appliedSettings != null) {
          settings = appliedSettings;
          updateKeyboardAndLabels();
        }
        configRoot.getScene().setRoot(rootPane);
        return null;
      });
      FXMLLoader loader = new FXMLLoader(getClass().getResource("WoerdleSettings.fxml"));
      loader.setController(configViewController);
      try {
        configRoot = loader.load();
      } catch (IOException ioex) {
        System.err.println("Couldn't load WoerdleSettings.fxml: " + ioex);
      }
    }
    if (configRoot != null) {
      configViewController.setSettings(settings);
      rootPane.getScene().setRoot(configRoot);
    }
  }

  private void applyKeyboard() {
    keyboardView.setSelectedKeyboard(null);
    keyboardView.setSelectedKeyboard(keyboard);
  }

  private void newGame() {
    newGame(getConfig());
  }
  
  private void newGame(WoerdleConfig config) {
    this.woerdle = new Woerdle(config);
    woerdle.start(randomWordProvider.apply(config.dict(), config.wordLength()));
    clearCurrentAttempt();
    initializeCharSlotsGrid();
    Platform.runLater(charSlotsGrid::requestFocus);
  }
  
  @FXML
  private void handleNewGame() {
    newGame();
  }

  private void initializeCharSlotsGrid() {
    charSlotsGrid.getChildren().clear();
    charSlotNodes.clear();
    int wordLength = getConfig().wordLength();
    int attemptCount = getConfig().maxAttemptCount();
    for (int rowNum = 0; rowNum < attemptCount; rowNum++) {
      for (int colNum = 0; colNum < wordLength; colNum++) {
        charSlotNodes.add(new CharSlotNode());
      }
    }
    charSlotsGrid.getChildren().addAll(charSlotNodes);
    int rowLength = getConfig().wordLength();
    for (int i = 0; i < charSlotNodes.size(); i++) {
      GridPane.setConstraints(charSlotNodes.get(i), i % rowLength, i / rowLength);
    }
    updateCharSlotsGrid();
  }

  private void clearCurrentAttempt() {
    currentAttempt = new StringBuilder();
    currentAttempt.setLength(getConfig().wordLength());
    currentAttemptPos = 0;
  }

  private boolean isCurrentAttemptComplete() {
    return currentAttempt.indexOf("\0") < 0;
  }

  private void updateCharSlotsGrid() {
    boolean illegalAttempt = (isCurrentAttemptComplete()
        && (! getConfig().dict().hasWord(currentAttempt.toString())));
    Map<Character, CharSlotKind> charKinds = new HashMap<>();
    for (int i = 0; i < charSlotNodes.size(); i++) {
      int rowNum = i / getConfig().wordLength();
      int colNum = i % getConfig().wordLength();
      var node = charSlotNodes.get(i);
      node.setValue(getValueForCharSlotNode(rowNum, colNum));
      CharSlotKind kind = getKindForCharSlotNode(rowNum, colNum);
      node.setCharSlotKind(kind);
      if (kind != null && rowNum < woerdle.getAttemptCount()) {
        char c = woerdle.getAttempt(rowNum).charAt(colNum);
        if ((! charKinds.containsKey(c)) || kind.ordinal() < charKinds.get(c).ordinal()) {
          charKinds.put(c, kind);
        }
      }
      node.setSelected(woerdle.isPlaying()
          && rowNum == woerdle.getAttemptCount() && colNum == currentAttemptPos);
      node.setExtraStyleClasses(rowNum == woerdle.getAttemptCount() && illegalAttempt
          ? List.of("char-slot-attempt-illegal-word") : null);
    }
    updateKeyboardColors(charKinds);
  }

  private void updateKeyboardColors(Map<Character, CharSlotKind> charKinds) {
    for (var row : keyboardView.getSelectedKeyboard().getRows()) {
      for (var keyBase : row.getKeys()) {
        if (keyBase instanceof Keyboard.Key) {
          updateKeyColors((Keyboard.Key) keyBase, charKinds);
        }
      }
    }
  }

  private void updateKeyColors(Keyboard.Key key, Map<Character, CharSlotKind> charKinds) {
    for (String c : key.getCharacters()) {
      if (c.length() == 1 && Character.isLetter(c.charAt(0))) {
        var kind = charKinds.get(Character.toUpperCase(c.charAt(0)));
        if (kind != null) {
          String styleClass = CharSlotNode.getCharSlotKindStyleClass(kind);
          key.getStyleClass().setAll(List.of(styleClass));
        } else {
          key.getStyleClass().clear();
        }
      }
    }
  }

  private Character getValueForCharSlotNode(int rowNum, int colNum) {
    if (rowNum < woerdle.getAttemptCount()) {
      return woerdle.getAttempt(rowNum).charAt(colNum);
    } else if (rowNum == woerdle.getAttemptCount() && colNum < currentAttempt.length()) {
      char c = currentAttempt.charAt(colNum);
      return c != 0 ? c : null;
    }
    return null;
  }

  private CharSlotKind getKindForCharSlotNode(int rowNum, int colNum) {
    if (rowNum < woerdle.getAttemptCount()) {
      return woerdle.getCharSlotKind(rowNum, colNum);
    }
    return null;
  }

  private void handleCharSlotKey(KeyEvent ev) {
    String chars = ev.getCharacter();
    if (chars.length() == 1 && Character.isLetter(chars.charAt(0))) {
      handleInsertChar(chars.charAt(0));
    } else {
      switch (ev.getCode()) {
        case BACK_SPACE -> handleRemoveLastChar();
        case ENTER -> handleAttempt();
        case LEFT -> handleMoveAttemptPos(-1);
        case RIGHT -> handleMoveAttemptPos(1);
        default -> { }
      }
    }
  }

  /**
   * Handles appending another character to the current attempt.
   */
  public void handleInsertChar(char c) {
    if (currentAttemptPos < currentAttempt.length()) {
      currentAttempt.setCharAt(currentAttemptPos, Character.toUpperCase(c));
      currentAttemptPos++;
      updateCharSlotsGrid();
    }
  }

  /**
   * Handles removing the last characters in the current attempt.
   */
  public void handleRemoveLastChar() {
    if (currentAttemptPos > 0) {
      currentAttemptPos--;
      currentAttempt.setCharAt(currentAttemptPos, '\0');
      updateCharSlotsGrid();
    }
  }

  /**
   * Handles (relative) movement of attempt cursor position.
   *
   * @param delta the relative movement
   */
  public void handleMoveAttemptPos(int delta) {
    int newPos = currentAttemptPos + delta;
    if (newPos >= 0 && newPos <= currentAttempt.length()) {
      currentAttemptPos = newPos;
      updateCharSlotsGrid();
    }
  }

  /**
   * Handles attempting the current attempt.
   */
  public void handleAttempt() {
    if (isCurrentAttemptComplete()) {
      String attempt = currentAttempt.toString(); 
      if (getConfig().dict().hasWord(attempt)) {
        woerdle.attempt(attempt);
        clearCurrentAttempt();
        updateCharSlotsGrid();
        applyKeyboard();
      }
    }
  }
}
