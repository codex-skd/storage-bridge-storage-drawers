package com.skd.storagebridge.storagedrawers.compat.apothic;

import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityController;
import com.skd.storagebridge.storagedrawers.compat.ControllerNetworkSearch;

import dev.shadowsoffire.apothic_enchanting.library.EnchLibraryTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * Triggered by right-clicking a Storage Drawers Controller's front face
 * (empty hand, or holding an {@code minecraft:enchanted_book} — either way the
 * player's whole inventory is scanned). Moves every enchanted book stack found
 * into an Apothic-Enchanting Library ({@code apothic_enchanting:library} /
 * {@code apothic_enchanting:ender_library}) reachable through the Controller's
 * own drawer network (see {@link ControllerNetworkSearch}). The Library does not
 * need to be directly adjacent to the Controller block itself, only to some block
 * that is part of that network.
 *
 * <p>One-directional: the Library consumes every book it accepts and never gives
 * anything back.</p>
 */
public final class LibraryDepositHandler {

    private LibraryDepositHandler() {
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
            return; // off-hand right-clicks never trigger the vanilla "deposit" gesture either
        }

        BlockState state = level.getBlockState(controllerPos);
        boolean hasFacing = state.hasProperty(HorizontalDirectionalBlock.FACING);
        Direction front = hasFacing ? state.getValue(HorizontalDirectionalBlock.FACING) : null;
        if (!hasFacing || front != event.getFace()) {
            return; // mirrors the vanilla "must click the front face" precondition
        }

        EnchLibraryTile library = ControllerNetworkSearch.findNetworked(level, controller, EnchLibraryTile.class);
        if (library == null) {
            return;
        }

        IItemHandler libraryHandler = library.getItemHandler(null);
        if (libraryHandler == null || libraryHandler.getSlots() < 1) {
            return;
        }

        Player player = event.getEntity();
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < Inventory.INVENTORY_SIZE; slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.isEmpty() || stack.getItem() != Items.ENCHANTED_BOOK) {
                continue;
            }
            while (!stack.isEmpty()) {
                ItemStack remainder = libraryHandler.insertItem(0, stack.copyWithCount(1), false);
                if (!remainder.isEmpty()) {
                    break; // Library rejected/full for this enchantment set, stop draining this stack
                }
                stack.shrink(1);
            }
        }
    }
}
