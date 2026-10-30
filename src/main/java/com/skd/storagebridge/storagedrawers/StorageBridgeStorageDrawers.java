package com.skd.storagebridge.storagedrawers;

import com.skd.storagebridge.storagedrawers.compat.apotheosis.GemCaseDepositHandler;
import com.skd.storagebridge.storagedrawers.compat.apothic.LibraryDepositHandler;
import com.skd.storagebridge.storagedrawers.compat.sophisticatedstorage.SophisticatedStorageDepositHandler;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(StorageBridgeStorageDrawers.MOD_ID)
public class StorageBridgeStorageDrawers {

    public static final String MOD_ID = "storage_bridge_storage_drawers";

    public StorageBridgeStorageDrawers(IEventBus modEventBus) {
        // Gameplay listeners live on the global game bus, not the mod lifecycle bus.
        NeoForge.EVENT_BUS.addListener(LibraryDepositHandler::onRightClickController);
        NeoForge.EVENT_BUS.addListener(SophisticatedStorageDepositHandler::onRightClickController);
        NeoForge.EVENT_BUS.addListener(GemCaseDepositHandler::onRightClickController);
    }
}
