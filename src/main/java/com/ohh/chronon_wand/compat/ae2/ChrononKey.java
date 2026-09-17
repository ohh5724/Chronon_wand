package com.ohh.chronon_wand.compat.ae2;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.ohh.chronon_wand.ChrononWand;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class ChrononKey extends AEKey {
    public static final ChrononKey KEY = new ChrononKey();
    public static final MapCodec<ChrononKey> MAP_CODEC = MapCodec.unit(KEY);
    public static final Codec<ChrononKey> CODEC = MAP_CODEC.codec();
    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(ChrononWand.MOD_ID, "chronons");

    private ChrononKey() {}

    @Override
    public AEKeyType getType() {
        return ChrononKeyType.TYPE;
    }

    @Override
    public AEKey dropSecondary() {
        return this;
    }

    @Override
    public CompoundTag toTag(HolderLookup.Provider provider) {
        return (CompoundTag) CODEC.encodeStart(provider.createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE), this).getOrThrow();
    }

    @Override
    public Object getPrimaryKey() {
        return this;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void writeToPacket(RegistryFriendlyByteBuf buffer) {
    }

    @Override
    public String getModId() {
        return ChrononWand.MOD_ID;
    }

    @Override
    protected Component computeDisplayName() {
        return Component.translatable("key.chronon_wand.chronons");
    }

    @Override
    public void addDrops(long amount, List<ItemStack> drops, Level level, BlockPos pos) {
    }

    @Override
    public boolean hasComponents() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ChrononKey;
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }

    @Override
    public String toString() {
        return "ChrononKey";
    }
}
