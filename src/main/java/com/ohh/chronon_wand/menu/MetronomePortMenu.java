package com.ohh.chronon_wand.menu;

import com.ohh.chronon_wand.ModBlocks;
import com.ohh.chronon_wand.block.PortChannel;
import com.ohh.chronon_wand.block.SideMode;
import com.ohh.chronon_wand.block.entity.MetronomePortBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

public class MetronomePortMenu extends AbstractContainerMenu {
    private final BlockPos pos;
    private final Level level;
    private final MetronomePortBlockEntity blockEntity;
    private final ContainerData data;

    public MetronomePortMenu(int containerId, Inventory inv, RegistryFriendlyByteBuf extraData) {
        this(containerId, inv, extraData.readBlockPos(), new SimpleContainerData(12));
    }

    public MetronomePortMenu(int containerId, Inventory inv, BlockPos pos, ContainerData data) {
        super(ModMenus.METRONOME_PORT_MENU.get(), containerId);
        this.pos = pos;
        this.level = inv.player.level();
        BlockEntity be = level.getBlockEntity(pos);
        this.blockEntity = be instanceof MetronomePortBlockEntity port ? port : null;
        this.data = data;
        this.addDataSlots(data);
    }

    public BlockPos getPos() {
        return pos;
    }

    public Level getLevel() {
        return level;
    }

    public MetronomePortBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public SideMode getSideMode(PortChannel channel, Direction direction) {
        int index = (channel == PortChannel.CHRONON_ENERGY ? 0 : 6) + direction.get3DDataValue();
        int val = data.get(index);
        SideMode[] modes = SideMode.values();
        if (val >= 0 && val < modes.length) {
            return modes[val];
        }
        return SideMode.BOTH;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, pos), player, ModBlocks.METRONOME_PORT.get());
    }
}
