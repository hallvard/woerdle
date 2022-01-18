package no.hal.woerdle.fxui.util;

import java.util.Properties;

import javafx.scene.control.Labeled;

public interface DictLabelsSupport {
  void applyLabels(Properties labels);

  public static void applyLabels(Properties labels, Labeled... labeleds) {
    for (var labeled : labeleds) {
      if (labeled != null) {
        labeled.setText(labels.getProperty(labeled.getId(), labeled.getText()));
      }
    }
  }
}