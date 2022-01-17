module no.hal.woerdle.fxui {

    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;

    requires de.jensd.fx.glyphs.commons;
    requires de.jensd.fx.glyphs.fontawesome;

    requires com.dlsc.keyboardfx;
    requires java.xml.bind;

    requires no.hal.woerdle.core;

    opens no.hal.woerdle.fxui to javafx.graphics, javafx.fxml;
}
