package com.skd.storagebridge.storagedrawers.compat;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import com.jaquadro.minecraft.storagedrawers.api.storage.IControlGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityController;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Shared breadth-first search over the physically-connected chain of
 * {@link INetworked} blocks starting at a Storage Drawers Controller's own
 * position — the same chain (drawers, frames, controller IO) the Controller
 * itself uses to find its drawers, bounded by the same {@code controllerRange}
 * config value. At every visited node, also checks its 6 neighbors for a block
 * entity matching the requested type.
 *
 * <p>Used by every {@code compat.*} integration in this mod (Apothic-Enchanting
 * Library, Sophisticated Storage chests/barrels, Apotheosis Gem Case, and future
 * ones) so the "reachable through the network, not just adjacent to the
 * Controller block" behaviour stays consistent across all of them.</p>
 */
public final class ControllerNetworkSearch {

    private ControllerNetworkSearch() {
    }

    public static <T extends BlockEntity> T findNetworked(Level level, BlockEntityController controller, Class<T> targetType) {
        BlockPos origin = controller.getBlockPos();
        int range = ModCommonConfig.INSTANCE.CONTROLLER.controllerRange.get();

        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> discovered = new HashSet<>();
        queue.add(origin);
        discovered.add(origin);

        while (!queue.isEmpty()) {
            BlockPos coord = queue.poll();

            int depth = Math.max(Math.max(
                    Math.abs(coord.getX() - origin.getX()),
                    Math.abs(coord.getY() - origin.getY())),
                    Math.abs(coord.getZ() - origin.getZ()));
            if (depth > range) {
                continue;
            }
            if (!level.isLoaded(coord)) {
                continue;
            }

            Block block = level.getBlockState(coord).getBlock();
            if (!(block instanceof INetworked networked)) {
                continue;
            }
            IControlGroup group = networked.getBoundControlGroup();
            if (group != null && group != controller) {
                continue; // part of a different controller's network
            }

            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = coord.relative(direction);
                BlockEntity neighbor = level.getBlockEntity(neighborPos);
                if (targetType.isInstance(neighbor) && !neighbor.isRemoved()) {
                    return targetType.cast(neighbor);
                }
            }

            for (Direction direction : Direction.values()) {
                BlockPos next = coord.relative(direction);
                if (discovered.add(next)) {
                    queue.add(next);
                }
            }
        }

        return null;
    }
}
