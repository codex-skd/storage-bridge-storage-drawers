package com.skd.storagebridge.storagedrawers.compat.apotheosis;

import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityController;
import com.skd.storagebridge.storagedrawers.compat.ControllerNetworkSearch;

import dev.shadowsoffire.apotheosis.socket.gem.storage.GemCaseTile;
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
import net.neoforged.neoforge.items.ItemHandlerHelper;

/**
 * Triggered by the same right-click gesture as {@code LibraryDepositHandler}
 * and {@code SophisticatedStorageDepositHandler} (Storage Drawers Controller's
 * front face, main hand). Moves unsocketed gem stacks from the player's
 * inventory into an Apotheosis Gem Case or Ender Gem Case
 * ({@link GemCaseTile} covers both {@code BasicGemCaseTile} and
 * {@code EnderGemCaseTile}) reachable through the Controller's own drawer
 * network (see {@link ControllerNetworkSearch}).
 *
 * <p>No item-type filtering is done here: the Gem Case's own
 * {@code IItemHandler} already rejects anything that isn't a valid unsocketed
 * gem (returns the stack unchanged), so this handler simply tries to insert
 * every non-empty, non-book stack and keeps whatever the Gem Case actually
 * accepted. One-directional: gems deposited this way are not extracted back
 * out by this mod.</p>
 */
public final class GemCaseDepositHandler {

    private GemCaseDepositHandler() {
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

        GemCaseTile gemCase = ControllerNetworkSearch.findNetworked(level, controller, GemCaseTile.class);
        if (gemCase == null) {
            return;
        }

        IItemHandler gemCaseHandler = gemCase.getItemHandler(null);
        if (gemCaseHandler == null) {
            return;
        }

        Player player = event.getEntity();
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < Inventory.INVENTORY_SIZE; slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.isEmpty() || stack.getItem() == Items.ENCHANTED_BOOK) {
                continue; // books are handled by the Apothic-Enchanting Library integration
            }

            int before = stack.getCount();
            ItemStack remainder = ItemHandlerHelper.insertItemStacked(gemCaseHandler, stack.copy(), false);
            int inserted = before - remainder.getCount();
            if (inserted > 0) {
                stack.shrink(inserted);
            }
        }
    }
}
