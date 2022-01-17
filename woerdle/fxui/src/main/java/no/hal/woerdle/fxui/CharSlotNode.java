package no.hal.woerdle.fxui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import no.hal.woerdle.core.Woerdle.CharSlotKind;

/**
 * A node for character slots.
 */
public class CharSlotNode extends Label {

  private Character value = null;
  private CharSlotKind kind = null;
  private boolean selected = false;

  private List<String> extraStyleClasses;

  /**
   * Initialises the size and alignment.
   */
  public CharSlotNode() {
    setPrefSize(60, 80);
    setAlignment(Pos.CENTER);
    updateView();
  }

  public Character getValue() {
    return value;
  }

  /**
   * Sets extra style classes that are applied to this node. May be null.
   *
   * @param extraStyleClasses the extra style classes
   */
  public void setExtraStyleClasses(List<String> extraStyleClasses) {
    this.extraStyleClasses =
        (extraStyleClasses != null ? new ArrayList<>(extraStyleClasses) : null);
    updateView();
  }

  /**
   * Sets the character value.
   *
   * @param value the new character value
   */
  public void setValue(Character value) {
    if (! Objects.equals(value, this.value)) {
      this.value = value;
      updateView();
    }
  }

  public CharSlotKind getCharSlotKind() {
    return kind;
  }

  /**
   * Sets the classification of the slot.
   *
   * @param kind the new classification
   */
  public void setCharSlotKind(CharSlotKind kind) {
    if (kind != this.kind) {
      this.kind = kind;
      updateView();
    }
  }

  public boolean isSelected() {
    return selected;
  }

  /**
   * Sets the selection (focus) status.
   *
   * @param selected the new selected status
   */
  public void setSelected(boolean selected) {
    if (selected != this.selected) {
      this.selected = selected;
      updateView();
    }
  }

  private static final Border SELECTED_BORDER = new Border(new BorderStroke(Color.BLACK,
      BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN));

  private void updateView() {
    Character c = getValue();
    setText(String.valueOf(c != null ? c : ' '));
    setBorder(isSelected() ? SELECTED_BORDER : Border.EMPTY);
    updateStyle();
  }

  private static final String CHAR_SLOT_NODE_STYLE_CLASS = "char-slot-node";

  private void updateStyle() {
    var charSlotKindStyleClass = "char-slot-kind-none";
    if (getCharSlotKind() != null) {
      charSlotKindStyleClass = switch (kind) {
        case MISPLACED -> "char-slot-kind-misplaced";
        case CORRECT -> "char-slot-kind-correct";
        case WRONG -> "char-slot-kind-wrong";
        default -> "char-slot-kind-none";
      };
    }
    getStyleClass().setAll(extraStyleClasses != null ? extraStyleClasses : Collections.emptyList());
    getStyleClass().addAll(List.of(CHAR_SLOT_NODE_STYLE_CLASS, charSlotKindStyleClass));
  }

  /**
   * Gets the css style class used for the provided kind.
   *
   * @return the css style class used for kind
   */
  public static String getCharSlotKindStyleClass(CharSlotKind kind) {
    return switch (kind) {
      case MISPLACED -> "char-slot-kind-misplaced";
      case CORRECT -> "char-slot-kind-correct";
      case WRONG -> "char-slot-kind-wrong";
      default -> "char-slot-kind-none";
    };
  }
}
