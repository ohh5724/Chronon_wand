package com.ohh.chronon_wand.item;

public enum CellTier {
    TIER_1K("1k", 1024L, "ae2:cell_component_1k", false),
    TIER_4K("4k", 4096L, "ae2:cell_component_4k", false),
    TIER_16K("16k", 16384L, "ae2:cell_component_16k", false),
    TIER_64K("64k", 65536L, "ae2:cell_component_64k", false),
    TIER_256K("256k", 262144L, "ae2:cell_component_256k", false),

    TIER_1M("1m", 1048576L, "megacells:cell_component_1m", true),
    TIER_4M("4m", 4194304L, "megacells:cell_component_4m", true),
    TIER_16M("16m", 16777216L, "megacells:cell_component_16m", true),
    TIER_64M("64m", 67108864L, "megacells:cell_component_64m", true),
    TIER_256M("256m", 268435456L, "megacells:cell_component_256m", true);

    public static final int BYTES_PER_CHRONON_RATIO = 8; // 1 Byte = 8 Chronons

    private final String name;
    private final long totalBytes;
    private final String componentId;
    private final boolean mega;

    CellTier(String name, long totalBytes, String componentId, boolean mega) {
        this.name = name;
        this.totalBytes = totalBytes;
        this.componentId = componentId;
        this.mega = mega;
    }

    public String getName() {
        return name;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public long getCapacity() {
        return totalBytes * BYTES_PER_CHRONON_RATIO;
    }

    public String getComponentId() {
        return componentId;
    }

    public boolean isMega() {
        return mega;
    }
}

