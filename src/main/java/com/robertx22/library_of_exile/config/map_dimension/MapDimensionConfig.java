package com.robertx22.library_of_exile.config.map_dimension;

import com.robertx22.library_of_exile.dimension.MapDimensions;
import com.robertx22.library_of_exile.main.ApiForgeEvents;
import com.robertx22.library_of_exile.main.CommonInit;
import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.util.LazyClass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;

public class MapDimensionConfig {

    public static HashMap<String, MapDimensionConfig> map = new HashMap<>();


    public static MapDimensionConfig get(ResourceLocation mapId) {
        return map.get(mapId.toString());
    }

    public ForgeConfigSpec.ConfigValue<String> ALLOWED_BLOCK_BREAK_TAG;
    public ForgeConfigSpec.ConfigValue<String> DISABLED_BLOCK_INTERACT_TAG;


    MapDimensionConfig(ForgeConfigSpec.Builder b) {
        b.comment("Map Dimension Config")
                .push("general");

        ALLOWED_BLOCK_BREAK_TAG = b
                .comment("Blocks in this tag will be breakable. This config isn't meant to be edited! Edit the tag datapack instead!\nUse this for stuff like Grave mod blocks")
                .define("ALLOWED_BLOCK_BREAK_TAG", Ref.MODID + ":" + "map_allowed_block_break");

        DISABLED_BLOCK_INTERACT_TAG = b
                .comment("Blocks in this tag will NOT be interactable. This config isn't meant to be edited! Edit the tag datapack instead!")
                .define("DISABLED_BLOCK_INTERACT_TAG", Ref.MODID + ":" + "map_disable_block_interact");

        b.pop();
    }

    public LazyClass<TagKey<Block>> LAZY_ALLOWED_BLOCKS = new LazyClass<>(() -> BlockTags.create(new ResourceLocation(ALLOWED_BLOCK_BREAK_TAG.get())));
    public LazyClass<TagKey<Block>> LAZY_BLOCKED_INTERACT_BLOCKS = new LazyClass<>(() -> BlockTags.create(new ResourceLocation(DISABLED_BLOCK_INTERACT_TAG.get())));


    static boolean initEvents = false;


    // registers the config and all map related events, but events only once
    // this way the events wont run if library mod is installed without any mods that use the events
    public static void register(ResourceLocation mapId) {

        final Pair<MapDimensionConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(MapDimensionConfig::new);
        var SPEC = specPair.getRight();
        var CONFIG = specPair.getLeft();
        map.put(mapId.toString(), CONFIG);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SPEC, CommonInit.defaultConfigName(ModConfig.Type.SERVER, mapId.getNamespace() + "_" + mapId.getPath()));

        if (!initEvents) {
            initEvents = true;

            ApiForgeEvents.registerForgeEvent(BlockEvent.BreakEvent.class, event -> {
                try {
                    if (MapDimensions.isMap(event.getPlayer().level())) {
                        if (!event.getState().is(MapDimensionConfig.get(mapId).LAZY_ALLOWED_BLOCKS.get())) {
                            event.setCanceled(true);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            ApiForgeEvents.registerForgeEvent(PlayerInteractEvent.RightClickBlock.class, event -> {
                try {
                    Player p = event.getEntity();
                    if (MapDimensions.isMap(p.level())) {
                        BlockState block = p.level().getBlockState(event.getPos());
                        if (block.is(MapDimensionConfig.get(mapId).LAZY_BLOCKED_INTERACT_BLOCKS.get())) {
                            event.setCanceled(true);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

    }


}
