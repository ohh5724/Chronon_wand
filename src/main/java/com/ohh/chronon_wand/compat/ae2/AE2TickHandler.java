package com.ohh.chronon_wand.compat.ae2;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.blockentity.misc.GrowthAcceleratorBlockEntity;
import appeng.me.helpers.IGridConnectedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AE2TickHandler {

    public static boolean canTick(BlockEntity be) {
        if (be == null || be.isRemoved()) {
            return false;
        }

        // 1. Direct ServerTickingBlockEntity (e.g. Crank, LightDetector, QuantumBridge, etc.)
        if (be instanceof ServerTickingBlockEntity) {
            return true;
        }

        // 2. Direct IGridTickable (e.g. Inscriber, Molecular Assembler, Charger, Vibration Chamber, IO Port, etc.)
        if (be instanceof IGridTickable) {
            return true;
        }

        // 3. Grid-connected block entities with a ticking service (e.g. Growth Accelerator)
        if (be instanceof IGridConnectedBlockEntity gridBe) {
            if (be instanceof GrowthAcceleratorBlockEntity) {
                return true;
            }
            try {
                IManagedGridNode mainNode = gridBe.getMainNode();
                if (mainNode != null && mainNode.isReady()) {
                    IGridNode node = mainNode.getNode();
                    if (node != null && node.getService(IGridTickable.class) != null) {
                        return true;
                    }
                }
            } catch (Throwable ignored) {
            }
        }

        // 4. Multipart cable bus hosts with attached parts (e.g. Import Bus, Export Bus)
        if (be instanceof IPartHost partHost) {
            try {
                for (Direction dir : Direction.values()) {
                    IPart part = partHost.getPart(dir);
                    if (isPartTickable(part)) {
                        return true;
                    }
                }
                IPart centerPart = partHost.getPart(null);
                if (isPartTickable(centerPart)) {
                    return true;
                }
            } catch (Throwable ignored) {
            }
        }

        return false;
    }

    private static boolean isPartTickable(IPart part) {
        if (part == null) return false;
        if (part instanceof IGridTickable) return true;
        try {
            IGridNode node = part.getGridNode();
            return node != null && node.getService(IGridTickable.class) != null;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static boolean tick(Level level, BlockPos pos, BlockState state, BlockEntity be) {
        if (be == null || be.isRemoved()) {
            return false;
        }

        boolean ticked = false;

        // 1. ServerTickingBlockEntity
        if (be instanceof ServerTickingBlockEntity serverTicker) {
            try {
                serverTicker.serverTick();
                ticked = true;
            } catch (Throwable ignored) {
            }
        }

        // 2. IGridConnectedBlockEntity & IGridTickable
        IGridNode mainNodeInstance = null;
        if (be instanceof IGridConnectedBlockEntity gridBe) {
            try {
                IManagedGridNode mainNode = gridBe.getMainNode();
                if (mainNode != null && mainNode.isReady()) {
                    mainNodeInstance = mainNode.getNode();
                }
            } catch (Throwable ignored) {
            }
        }

        IGridTickable tickedDirectService = null;
        if (be instanceof IGridTickable tickable) {
            try {
                tickable.tickingRequest(mainNodeInstance, 1);
                tickedDirectService = tickable;
                ticked = true;
            } catch (Throwable ignored) {
            }
        }

        if (mainNodeInstance != null) {
            try {
                IGridTickable service = mainNodeInstance.getService(IGridTickable.class);
                if (service != null && service != tickedDirectService) {
                    service.tickingRequest(mainNodeInstance, 1);
                    ticked = true;
                }
            } catch (Throwable ignored) {
            }
        }

        // 3. IPartHost parts (CableBus, etc.)
        if (be instanceof IPartHost partHost) {
            try {
                for (Direction dir : Direction.values()) {
                    if (tickPart(partHost.getPart(dir))) {
                        ticked = true;
                    }
                }
                if (tickPart(partHost.getPart(null))) {
                    ticked = true;
                }
            } catch (Throwable ignored) {
            }
        }

        return ticked;
    }

    private static boolean tickPart(IPart part) {
        if (part == null) return false;
        boolean ticked = false;
        IGridNode node = null;
        try {
            node = part.getGridNode();
        } catch (Throwable ignored) {
        }

        IGridTickable direct = null;
        if (part instanceof IGridTickable tickable) {
            try {
                tickable.tickingRequest(node, 1);
                direct = tickable;
                ticked = true;
            } catch (Throwable ignored) {
            }
        }

        if (node != null) {
            try {
                IGridTickable service = node.getService(IGridTickable.class);
                if (service != null && service != direct) {
                    service.tickingRequest(node, 1);
                    ticked = true;
                }
            } catch (Throwable ignored) {
            }
        }

        return ticked;
    }
}
