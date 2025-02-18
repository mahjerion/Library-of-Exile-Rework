package com.robertx22.library_of_exile.main;

import com.robertx22.library_of_exile.entries.TeleportBackBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

public class ExileLibEntries {

    public static RegistryObject<TeleportBackBlock> TELEPORT_BACK_BLOCK = CommonInit.BLOCKS.register("teleport_back", () -> new TeleportBackBlock());
    public static RegistryObject<BlockItem> TELEPORT_BACK_BLOCK_ITEM = CommonInit.ITEMS.register("teleport_back", () -> new BlockItem(TELEPORT_BACK_BLOCK.get(), new Item.Properties().stacksTo(64)));

    public static void init() {

    }
}
