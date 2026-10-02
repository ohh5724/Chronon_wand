package com.ohh.chronon_wand.menu;

import com.ohh.chronon_wand.ModBlocks;
import com.ohh.chronon_wand.block.entity.ChrononAcceleratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ChrononAcceleratorMenu extends AbstractContainerMenu {
    private final BlockPos pos;
    private final Level level;
    private final ChrononAcceleratorBlockEntity blockEntity;
    private final ContainerData data;

    public ChrononAcceleratorMenu(int containerId, Inventory inv, RegistryFriendlyByteBuf extraData) {
        this(containerId, inv, extraData.readBlockPos(), readInitialData(extraData));
    }

    private static ContainerData readInitialData(RegistryFriendlyByteBuf buf) {
        SimpleContainerData data = new SimpleContainerData(14);
        if (buf.readableBytes() >= 53) {
            int power = buf.readInt();
            int maxPower = buf.readInt();
            data.set(0, power & 0xFFFF);
            data.set(1, (power >> 16) & 0xFFFF);
            data.set(2, maxPower & 0xFFFF);
            data.set(3, (maxPower >> 16) & 0xFFFF);
            data.set(4, buf.readInt());
            data.set(5, buf.readInt());
            data.set(6, (int) Math.round(buf.readDouble() * 2));
            data.set(7, (int) Math.round(buf.readDouble() * 2));
            data.set(8, (int) Math.round(buf.readDouble() * 2));
            data.set(9, buf.readInt());
            data.set(10, buf.readInt());
            data.set(11, buf.readInt());
            data.set(12, buf.readBoolean() ? 1 : 0);
            data.set(13, buf.readInt());
        }
        return data;
    }

    public ChrononAcceleratorMenu(int containerId, Inventory inv, BlockPos pos, ContainerData data) {
        super(ModMenus.CHRONON_ACCELERATOR_MENU.get(), containerId);
        this.pos = pos;
        this.level = inv.player.level();
        BlockEntity be = level.getBlockEntity(pos);
        this.blockEntity = be instanceof ChrononAcceleratorBlockEntity ce ? ce : null;
        this.data = data;
        this.addDataSlots(data);
    }

    public BlockPos getPos() {
        return pos;
    }

    public Level getLevel() {
        return level;
    }

    public ChrononAcceleratorBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public int getPower() {
        return (data.get(1) << 16) | (data.get(0) & 0xFFFF);
    }

    public int getMaxPower() {
        return (data.get(3) << 16) | (data.get(2) & 0xFFFF);
    }

    public int getSpeedTierIndex() {
        return data.get(4);
    }

    public int getRedstoneMode() {
        return data.get(5);
    }

    public double getXRadius() {
        return data.get(6) / 2.0;
    }

    public double getYRadius() {
        return data.get(7) / 2.0;
    }

    public double getZRadius() {
        return data.get(8) / 2.0;
    }

    public int getXOffset() {
        return data.get(9);
    }

    public int getYOffset() {
        return data.get(10);
    }

    public int getZOffset() {
        return data.get(11);
    }

    public boolean getRenderArea() {
        return data.get(12) != 0;
    }

    public int getAcceleratedBlockCount() {
        return data.get(13);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, pos), player, ModBlocks.CHRONON_ACCELERATOR.get());
    }
}
