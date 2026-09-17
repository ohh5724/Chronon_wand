package com.ohh.chronon_wand.compat.ae2;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import com.mojang.serialization.MapCodec;
import com.ohh.chronon_wand.ChrononWand;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ChrononKeyType extends AEKeyType {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(ChrononWand.MOD_ID, "chronons");
    public static final ChrononKeyType TYPE = new ChrononKeyType();

    public ChrononKeyType() {
        super(ID, ChrononKey.class, Component.translatable("key.chronon_wand.chronons"));
    }

    @Override
    public MapCodec<? extends AEKey> codec() {
        return ChrononKey.MAP_CODEC;
    }

    @Override
    public AEKey readFromPacket(RegistryFriendlyByteBuf buffer) {
        return ChrononKey.KEY;
    }

    @Override
    public int getAmountPerByte() {
        return 8; // 1 Byte = 8 Chronons
    }

    @Override
    public int getAmountPerOperation() {
        return 1000;
    }

    @Override
    public String getUnitSymbol() {
        return " C";
    }

    @Override
    public int getAmountPerUnit() {
        return 1;
    }
}

