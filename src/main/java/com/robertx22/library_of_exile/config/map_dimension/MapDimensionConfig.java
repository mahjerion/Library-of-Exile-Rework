package com.robertx22.library_of_exile.config.map_dimension;

import com.robertx22.library_of_exile.dimension.MapDimensionInfo;
import com.robertx22.library_of_exile.dimension.MapDimensions;
import com.robertx22.library_of_exile.main.ApiForgeEvents;
import com.robertx22.library_of_exile.main.CommonInit;
import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.util.LazyClass;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

public class MapDimensionConfig {


    public ForgeConfigSpec.ConfigValue<String> ALLOWED_BLOCK_BREAK_TAG;
    public ForgeConfigSpec.ConfigValue<String> DISABLED_BLOCK_INTERACT_TAG;
    public ForgeConfigSpec.ConfigValue<String> BANNED_ITEMS_TAG;
    public ForgeConfigSpec.ConfigValue<String> DEFAULT_DATA_BLOCK;

    public ForgeConfigSpec.IntValue CHUNK_PROCESS_RADIUS;
    public ForgeConfigSpec.IntValue CHUNK_SPAWN_RADIUS;
    public ForgeConfigSpec.BooleanValue DESPAWN_INCORRECT_MOBS;
    public ForgeConfigSpec.BooleanValue DISABLE_WORLDBORDER_OVERRIDE;
    public ForgeConfigSpec.BooleanValue DIMENSION_MOBS_ENVIRO_IMMUNITY;


    MapDimensionConfig(ForgeConfigSpec.Builder b, MapDimensionConfigDefaults opt, String id) {
        b.comment("Map Dimension Config, Note: These configs are ONLY for this dimension!")
                .push(id);

        DEFAULT_DATA_BLOCK = b
                .comment("Sometimes structures have old/wrong data blocks, instead of skipping them, we can instead use them to spawn a replacement.\nBy default, a small mob pack will spawn instead.\nAdvised to leave this as is")
                .define("DEFAULT_DATA_BLOCK", "mob");

        ALLOWED_BLOCK_BREAK_TAG = b
                .comment("Blocks in this tag will be breakable. This config isn't meant to be edited! Edit the tag datapack instead!\nUse this for stuff like Grave mod blocks")
                .define("ALLOWED_BLOCK_BREAK_TAG", Ref.MODID + ":" + "map_allowed_block_break");

        DISABLED_BLOCK_INTERACT_TAG = b
                .comment("Blocks in this tag will NOT be interactable. This config isn't meant to be edited! Edit the tag datapack instead!\n As an example, by default dispensers can't be interacted with so players can't steal items from them.")
                .define("DISABLED_BLOCK_INTERACT_TAG", Ref.MODID + ":" + "map_disable_block_interact");

        BANNED_ITEMS_TAG = b
                .comment("Items in this Tag will be unusable with right click in this dimension. This config isn't meant to be edited! Edit the tag datapack instead!\n As an example, by default chorus fruit and other teleportation items are banned..")
                .define("BANNED_ITEMS_TAG", Ref.MODID + ":" + "banned_map_items");

        CHUNK_PROCESS_RADIUS = b
                .comment("The chunk radius in which map data blocks will be turned into map content while in maps. Depending on map type, different values can be good\n" +
                        "For example Arena-type maps you probably want the number to be high so all the stuff generates right away\n" +
                        "But for exploration-type big maps, you probably don't want mobs to spawn 5 chunks away and despawn\n" +
                        "0 Radius means only the chunk the player is currently in will be processed")
                .defineInRange("CHUNK_PROCESS_RADIUS", opt.chunkProcessRadius, 0, 8);

        CHUNK_SPAWN_RADIUS = b
                .comment("Radius in which the data blocks will turn to actual content in map.")
                .defineInRange("CHUNK_SPAWN_RADIUS", opt.chunkSpawnRadius, 0, 8);

        DESPAWN_INCORRECT_MOBS = b
                .comment("Despawns or tries to stop spawning of mobs that shouldn't spawn in the dimension")
                .define("DESPAWN_INCORRECT_MOBS", true);


        DISABLE_WORLDBORDER_OVERRIDE = b
                .comment("By default this dimension has its worldborder overrided because these dimensions are meant to be infinite.\n" +
                        "It's recommended to just wipe the dimension's save folder when needed instead as they're not meant to be built in anyway, so wiping them is no problem.\n" +
                        "This config is only here in case this feature causes more urgent bugs.")
                .define("DISABLE_WORLDBORDER_OVERRIDE", false);

        DIMENSION_MOBS_ENVIRO_IMMUNITY = b
                .comment("Makes mobs inside this dimension immune to enviromental damage.\n" +
                        "Recommended ON because otherwise a lot of dimension mobs will die to: wither roses, lava, water, wall damage etc.")
                .define("DIMENSION_MOBS_ENVIRO_IMMUNITY", true);

        b.pop();
    }

    public LazyClass<TagKey<Block>> LAZY_ALLOWED_BLOCKS = new LazyClass<>(() -> BlockTags.create(new ResourceLocation(ALLOWED_BLOCK_BREAK_TAG.get())));
    public LazyClass<TagKey<Block>> LAZY_BLOCKED_INTERACT_BLOCKS = new LazyClass<>(() -> BlockTags.create(new ResourceLocation(DISABLED_BLOCK_INTERACT_TAG.get())));
    public LazyClass<TagKey<Item>> LAZY_BANNED_ITEMS = new LazyClass<>(() -> ItemTags.create(new ResourceLocation(BANNED_ITEMS_TAG.get())));


    static boolean isDimension(ResourceLocation id, Level level) {
        return level.dimensionTypeId().location().equals(id);
    }

    static boolean tryGiveLeeWay(Entity en) {

        if (en instanceof Player p && p.isCreative()) {
            return false;
        }

        return true;
    }


    public static MapDimensionConfig register(MapDimensionInfo info, MapDimensionConfigDefaults opt) {
        ResourceLocation mapId = info.dimensionId;

        final Pair<MapDimensionConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(b -> new MapDimensionConfig(b, opt, mapId.toString()));
        var SPEC = specPair.getRight();
        var CONFIG = specPair.getLeft();
   
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SPEC, CommonInit.defaultConfigName(ModConfig.Type.SERVER, mapId.getNamespace() + "_dimension"));


        ApiForgeEvents.registerForgeEvent(PlayerInteractEvent.RightClickItem.class, event -> {

            if (!isDimension(mapId, event.getEntity().level()) || !MapDimensions.isMap(event.getEntity().level())) {
                return;
            }

            if (event.getItemStack().is(CONFIG.LAZY_BANNED_ITEMS.get())) {
                if (tryGiveLeeWay(event.getEntity())) {
                    if (!event.getLevel().isClientSide) {
                        event.getEntity().sendSystemMessage(Component.literal("Item is banned in This Dimension: ")
                                .append(event.getItemStack().getDisplayName()).withStyle(ChatFormatting.BOLD));
                    }
                    event.setCanceled(true);
                }
            }
        });

        ApiForgeEvents.registerForgeEvent(BlockEvent.BreakEvent.class, event -> {
            try {
                if (!isDimension(mapId, event.getPlayer().level()) || !MapDimensions.isMap(event.getPlayer().level())) {
                    return;
                }
                if (!event.getState().is(CONFIG.LAZY_ALLOWED_BLOCKS.get())) {
                    if (tryGiveLeeWay(event.getPlayer())) {
                        event.setCanceled(true);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ApiForgeEvents.registerForgeEvent(LivingDestroyBlockEvent.class, event -> {
            try {
                if (!isDimension(mapId, event.getEntity().level()) || !MapDimensions.isMap(event.getEntity().level())) {
                    return;
                }
                if (!event.getState().is(CONFIG.LAZY_ALLOWED_BLOCKS.get())) {
                    if (tryGiveLeeWay(event.getEntity())) {
                        event.setCanceled(true);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ApiForgeEvents.registerForgeEvent(BlockEvent.EntityPlaceEvent.class, event -> {
            try {
                var en = event.getEntity();

                if (!isDimension(mapId, event.getEntity().level()) || !MapDimensions.isMap(event.getEntity().level())) {
                    return;
                }
                if (tryGiveLeeWay(en)) {
                    event.setCanceled(true);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ApiForgeEvents.registerForgeEvent(FillBucketEvent.class, event -> {
            try {
                Player p = event.getEntity();

                if (!isDimension(mapId, event.getEntity().level()) || !MapDimensions.isMap(event.getEntity().level())) {
                    return;
                }
                if (tryGiveLeeWay(p)) {
                    event.setCanceled(true);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ApiForgeEvents.registerForgeEvent(ExplosionEvent.Detonate.class, event -> {
            try {
                if (!isDimension(mapId, event.getLevel()) || !MapDimensions.isMap(event.getLevel())) {
                    return;
                }
                // we don't want explosions in maps
                event.getAffectedBlocks().clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ApiForgeEvents.registerForgeEvent(EntityMobGriefingEvent.class, event -> {
            try {
                if (!isDimension(mapId, event.getEntity().level()) || !MapDimensions.isMap(event.getEntity().level())) {
                    return;
                }
                // we don't want explosions in maps
                event.setResult(Event.Result.DENY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ApiForgeEvents.registerForgeEvent(PlayerInteractEvent.RightClickBlock.class, event -> {
            try {
                Player p = event.getEntity();

                if (!isDimension(mapId, event.getEntity().level()) || !MapDimensions.isMap(event.getEntity().level())) {
                    return;
                }

                BlockState block = p.level().getBlockState(event.getPos());

                if (block.is(CONFIG.LAZY_BLOCKED_INTERACT_BLOCKS.get())) {
                    if (tryGiveLeeWay(p)) {
                        event.setCanceled(true);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ApiForgeEvents.registerForgeEvent(TickEvent.PlayerTickEvent.class, event ->
        {
            Player p = event.player;

            if (p.tickCount % 20 != 0) {
                return;
            }
            if (p.tickCount < 20) {
                return;
            }
            if (p.level().isClientSide || event.phase != TickEvent.Phase.END) {
                return;
            }
            if (!isDimension(mapId, p.level()) || !MapDimensions.isMap(p.level())) {
                return;
            }
            ProcessMapChunks.process(p, info, CONFIG, ChunkProcessType.NORMAL);
        });

        List<MobSpawnType> blockedSpawnTypes = Arrays.asList(
                MobSpawnType.BREEDING,
                MobSpawnType.BUCKET,
                MobSpawnType.CHUNK_GENERATION,
                MobSpawnType.NATURAL,
                MobSpawnType.REINFORCEMENT
        );

        ApiForgeEvents.registerForgeEvent(MobSpawnEvent.SpawnPlacementCheck.class, event ->
        {
            try {
                var world = event.getLevel().getLevel();

                if (world.isClientSide) {
                    return;
                }
                if (!isDimension(mapId, world) || !MapDimensions.isMap(world)) {
                    return;
                }
                if (CONFIG.DESPAWN_INCORRECT_MOBS.get()) {
                    return;
                }
                // let's not accidentally stop players from spawning just in case
                if (event.getEntityType() == EntityType.PLAYER) {
                    return;
                }
                var type = event.getSpawnType();

                if (blockedSpawnTypes.contains(type)) {
                    event.setResult(Event.Result.DENY);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        ApiForgeEvents.registerForgeEvent(LivingAttackEvent.class, event -> {
            try {
                var en = event.getEntity();

                if (!isDimension(mapId, event.getEntity().level()) || !MapDimensions.isMap(event.getEntity().level())) {
                    return;
                }
                if (en instanceof Player) {
                    return;
                }
                if (CONFIG.DIMENSION_MOBS_ENVIRO_IMMUNITY.get()) {
                    if (event.getSource().getEntity() instanceof LivingEntity == false) {
                        event.setCanceled(true);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return CONFIG;
    }


}
