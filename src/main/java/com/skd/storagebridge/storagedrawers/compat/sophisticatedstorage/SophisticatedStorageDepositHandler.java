package com.skd.storagebridge.storagedrawers.compat.sophisticatedstorage;

import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityController;
import com.skd.storagebridge.storagedrawers.compat.ControllerNetworkSearch;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.p3pp3rf1y.sophisticatedstorage.block.WoodStorageBlockEntity;

/**
 * Triggered by the same right-click gesture as {@code LibraryDepositHandler}
 * (Storage Drawers Controller's front face, main hand). Moves items from the
 * player's inventory into a Sophisticated Storage chest or barrel
 * ({@link WoodStorageBlockEntity} covers {@code ChestBlockEntity},
 * {@code BarrelBlockEntity} and {@code LimitedBarrelBlockEntity} — not Shulker
 * Boxes or the Storage Controller) reachable through the Controller's own
 * drawer network (see {@link ControllerNetworkSearch}).
 *
 * <p>Only items that the target chest/barrel <em>already contains at least one
 * matching stack of</em> are moved — this mirrors how a Storage Drawers drawer
 * only accepts items matching its assigned material, so Storage Bridge (Storage Drawers) never
 * turns an empty, unfiltered barrel into a generic dump for unrelated items.
 * Enchanted books are skipped here; those go to an Apothic-Enchanting Library
 * instead (see {@code LibraryDepositHandler}).</p>
 */
public final class SophisticatedStorageDepositHandler {

    private SophisticatedStorageDepositHandler() {
    }

    public static void onRightClickController(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        if (level.isClientSide()) {
            return;
        }

        BlockPos controllerPos = event.getPos();
        if (!(level.getBlockEntity(controllerPos) instanceof BlockEntityController controller)) {
            return; // not a Controller at all, stay silent to avoid spamming on unrelated clicks
        }

        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        BlockState state = level.getBlockState(controllerPos);
        boolean hasFacing = state.hasProperty(HorizontalDirectionalBlock.FACING);
        Direction front = hasFacing ? state.getValue(HorizontalDirectionalBlock.FACING) : null;
        if (!hasFacing || front != event.getFace()) {
            return;
        }

        WoodStorageBlockEntity storage = ControllerNetworkSearch.findNetworked(level, controller, WoodStorageBlockEntity.class);
        if (storage == null) {
            return;
        }

        IItemHandler storageHandler = storage.getExternalItemHandler(null);
        if (storageHandler == null) {
            return;
        }

        Player player = event.getEntity();
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < Inventory.INVENTORY_SIZE; slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.isEmpty() || stack.getItem() == Items.ENCHANTED_BOOK) {
                continue; // books are handled by the Apothic-Enchanting Library integration
            }
            if (!hasMatchingItem(storageHandler, stack.getItem())) {
                continue; // only move items the chest/barrel already stores, never dump unrelated items
            }

            int before = stack.getCount();
            ItemStack remainder = ItemHandlerHelper.insertItemStacked(storageHandler, stack.copy(), false);
            int inserted = before - remainder.getCount();
            if (inserted > 0) {
                stack.shrink(inserted);
            }
        }
    }

    private static boolean hasMatchingItem(IItemHandler handler, Item item) {
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack existing = handler.getStackInSlot(slot);
            if (!existing.isEmpty() && existing.getItem() == item) {
                return true;
            }
        }
        return false;
    }
}
