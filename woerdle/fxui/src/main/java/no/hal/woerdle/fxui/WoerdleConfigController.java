package no.hal.woerdle.fxui;

import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import no.hal.woerdle.core.WoerdleConfig;

/**
 * Controller for Gjetord.
 */
public class WoerdleConfigController {

  private WoerdleConfig config;
  private Callback<WoerdleConfig, Void> configCallback = null;

  public WoerdleConfig getConfig() {
    return config;
  }

  private void setConfig(WoerdleConfig config) {
    this.config = config;
    if (configCallback != null) {
      configCallback.call(getConfig());
    }
  }

  private void updateConfig() {
    setConfig(new WoerdleConfig(wordLengthSpinner.getValue(), attemptCountSpinner.getValue(),
        null));
  }

  public void setConfigCallback(Callback<WoerdleConfig, Void> configCallback) {
    this.configCallback = configCallback;
  }
  
  @FXML
  private Spinner<Integer> wordLengthSpinner;
  
  @FXML
  private Spinner<Integer> attemptCountSpinner;
  
  @FXML
  private GridPane charSlotsGrid;

  @FXML
  private void initialize() {
    wordLengthSpinner.getValueFactory().setValue(5);
    attemptCountSpinner.getValueFactory().setValue(6);
    updateConfig();
  }


  @FXML
  private void handleWordLengthSpinnerChange() {
    updateConfig();
  }

  @FXML
  private void handleAttemptCountSpinnerChange() {
    updateConfig();
  }
}
