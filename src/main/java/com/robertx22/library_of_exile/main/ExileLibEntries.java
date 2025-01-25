package com.robertx22.library_of_exile.main;

import com.robertx22.library_of_exile.entries.InvisibleDataBlock;
import com.robertx22.library_of_exile.entries.InvisibleDataBlockEntity;
import com.robertx22.library_of_exile.entries.TeleportBackBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

public class ExileLibEntries {

    public static RegistryObject<TeleportBackBlock> TELEPORT_BACK_BLOCK = CommonInit.BLOCKS.register("teleport_back", () -> new TeleportBackBlock());
    public static RegistryObject<InvisibleDataBlock> INVISIBLE_DATA_BLOCK = CommonInit.BLOCKS.register("invis_data_block", () -> new InvisibleDataBlock());


    public static RegistryObject<BlockEntityType<InvisibleDataBlockEntity>> INVISIBLE_DATA_BLOCK_ENTITY = CommonInit.BLOCK_ENTITIES.register("invisible_data_block", () -> BlockEntityType.Builder.of(InvisibleDataBlockEntity::new, INVISIBLE_DATA_BLOCK.get()).build(null));

    public static RegistryObject<BlockItem> TELEPORT_BACK_BLOCK_ITEM = CommonInit.ITEMS.register("teleport_back", () -> new BlockItem(TELEPORT_BACK_BLOCK.get(), new Item.Properties().stacksTo(64)));

    public static void init() {

    }
}
