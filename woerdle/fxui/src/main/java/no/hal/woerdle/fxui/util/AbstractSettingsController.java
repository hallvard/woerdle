package no.hal.woerdle.fxui.util;

import javafx.fxml.FXML;
import javafx.util.Callback;

/**
 * Generic controller for settings, with cancel and apply buttons.
 */
public abstract class AbstractSettingsController<T> {

  protected T settings;

  public T getSettings() {
    return settings;
  }

  public void setSettings(T settings) {
    this.settings = settings;
    updateView();
  }

  protected abstract void updateView();
  protected abstract void updateSettings();

  @FXML
  private void handleCancelAction() {
    back(null);
  }

  @FXML
  private void handleApplyAction() {
    updateSettings();
    back(settings);
  }

  private Callback<T, Void> backCallback;

  public void setBackCallback(Callback<T, Void> backCallback) {
      this.backCallback = backCallback;
  }

  protected void back(T t) {
    backCallback.call(t);
  }
}
