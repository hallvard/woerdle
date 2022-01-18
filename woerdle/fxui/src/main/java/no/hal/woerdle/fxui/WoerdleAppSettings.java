package no.hal.woerdle.fxui;

import no.hal.woerdle.core.WoerdleConfig;

public class WoerdleAppSettings {

    private String lang;
    private String dictName;
    private WoerdleConfig config;

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    public WoerdleConfig getConfig() {
        return config;
    }

    public void setConfig(WoerdleConfig config) {
        this.config = config;
    }
}
