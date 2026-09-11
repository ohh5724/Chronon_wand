package com.ohh.chronon_wand.block;

public enum PortChannel {
    CHRONON_ENERGY("gui.chronon_wand.tab.chronons"),
    CHRONON_FLUID("gui.chronon_wand.tab.chronon_fluid");

    private final String translationKey;

    PortChannel(String translationKey) {
        this.translationKey = translationKey;
    }

    public String getTranslationKey() {
        return translationKey;
    }
}
