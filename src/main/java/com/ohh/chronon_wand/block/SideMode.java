package com.ohh.chronon_wand.block;

public enum SideMode {
    BOTH("gui.chronon_wand.mode.both", 0xFF8E24AA),    // Purple
    INPUT("gui.chronon_wand.mode.input", 0xFFE53935),  // Red
    OUTPUT("gui.chronon_wand.mode.output", 0xFF1E88E5),// Blue
    NONE("gui.chronon_wand.mode.none", 0xFF616161);    // Gray

    private final String translationKey;
    private final int color;

    SideMode(String translationKey, int color) {
        this.translationKey = translationKey;
        this.color = color;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public int getColor() {
        return color;
    }

    public boolean canInsert() {
        return this == BOTH || this == INPUT;
    }

    public boolean canExtract() {
        return this == BOTH || this == OUTPUT;
    }

    public SideMode next() {
        return switch (this) {
            case BOTH -> INPUT;
            case INPUT -> OUTPUT;
            case OUTPUT -> NONE;
            case NONE -> BOTH;
        };
    }

    public SideMode previous() {
        return switch (this) {
            case BOTH -> NONE;
            case INPUT -> BOTH;
            case OUTPUT -> INPUT;
            case NONE -> OUTPUT;
        };
    }
}
