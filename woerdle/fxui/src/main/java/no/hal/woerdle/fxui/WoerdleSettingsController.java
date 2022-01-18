package no.hal.woerdle.fxui;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Labeled;
import javafx.scene.control.Spinner;
import no.hal.woerdle.core.WoerdleConfig;
import no.hal.woerdle.dict.Dict;
import no.hal.woerdle.dict.ResourceDict;
import no.hal.woerdle.fxui.util.AbstractSettingsController;
import no.hal.woerdle.fxui.util.DictLabelsSupport;

/**
 * Controller for woerdle settings.
 */
public class WoerdleSettingsController extends AbstractSettingsController<WoerdleAppSettings> implements DictLabelsSupport {

  @FXML private Labeled settingsCancelButton;
  @FXML private Labeled settingsApplyButton;

  @FXML private Labeled langLabel;
  @FXML private Labeled dictLabel;
  @FXML private Labeled wordLengthLabel;
  @FXML private Labeled attemptCountSLabel;
  
  @Override
  public void applyLabels(Properties labels) {
    DictLabelsSupport.applyLabels(labels, settingsCancelButton, settingsApplyButton, langLabel, dictLabel, wordLengthLabel, attemptCountSLabel);
  }

  private Properties woerdleProperties;

  public Properties getWoerdleProperties() {
    if (woerdleProperties == null) {
      woerdleProperties = getProperties("woerdle");
    }
    return woerdleProperties;
  }

  private Properties labelProperties = null;

  public Properties getProperties(String key) {
      try (InputStream input = getClass().getResourceAsStream(key + ".properties")) {
        if (input == null) {
          throw new IOException("No such resource: " + key + " properties");
        }
        Properties labels = new Properties();
        labels.load(new InputStreamReader(input, StandardCharsets.UTF_8));
        System.out.println("Read from " + key + ".properties: " + labels);
        return labels;
      } catch (IOException ioex) {
        System.err.println("Couldn't read " + key + ".properties resource");
        return null;
      }
  }

  public String getLangForName(String lang) {
    int langNum = (langNames != null ? langNames.indexOf(lang) : -1);
    return langNum >= 0 ? langs.get(langNum) : lang;
  }

  public Properties getLabelProperties(String lang) {
    return getProperties("labels-" + getLangForName(lang));
  }
  
  public Properties getLabelProperties() {
    if (labelProperties == null) {
      labelProperties = getLabelProperties(settings.getLang());
    }
    return labelProperties;
  }

  @FXML
  public void handleLangSelectorAction() {
    applyLabels(getLabelProperties(getLangForName(langSelector.getValue())));
  }

  @FXML
  private ChoiceBox<String> langSelector;

  @FXML
  private ComboBox<String> dictSelector;

  @FXML
  private Spinner<Integer> wordLengthSpinner;
  
  @FXML
  private Spinner<Integer> attemptCountSpinner;
  
  private List<String> langs;
  private List<String> langNames;

  @FXML
  private void initialize() {
    Properties props = getWoerdleProperties();
    langs = Arrays.asList(props.getProperty("langs").split(","));
    langNames = langs.stream().map(lang -> props.getProperty("lang-" + lang, lang)).toList();
    langSelector.getItems().setAll(langNames);

    var dicts = Arrays.asList(props.getProperty("dictNames").split(","));
    dictSelector.getItems().setAll(dicts);

    if (settings != null) {
      updateView();
    }
  }

  @Override
  protected void updateView() {
    // only update if initialize has been called
    if (langs != null) {
      langSelector.setValue(langNames.get(langs.indexOf(settings.getLang())));
      dictSelector.setValue(settings.getDictName());
      wordLengthSpinner.getValueFactory().setValue(settings.getConfig().wordLength());
      attemptCountSpinner.getValueFactory().setValue(settings.getConfig().maxAttemptCount());
    }
  }

  @Override
  protected void updateSettings() {
    settings.setLang(langs.get(langSelector.getSelectionModel().getSelectedIndex()));
    settings.setConfig(new WoerdleConfig(
      wordLengthSpinner.getValue(),
      attemptCountSpinner.getValue(),
      (resourceDict != null ? resourceDict : settings.getConfig().dict()))
    );
  }

  private Dict resourceDict = null;

  public Dict getDictForName(String dictName) {
    String resourceName = getWoerdleProperties().getProperty("dict-" + dictName, dictName);
    return new ResourceDict(resourceName);
  }

  @FXML
  public void handleDictSelectorAction() {
    String dictName = dictSelector.getSelectionModel().getSelectedItem();
    resourceDict = getDictForName(dictName);
  }
}
