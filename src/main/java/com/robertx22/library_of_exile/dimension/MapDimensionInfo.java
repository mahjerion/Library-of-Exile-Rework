package com.robertx22.library_of_exile.dimension;

import com.robertx22.library_of_exile.components.MapConnectionsCap;
import com.robertx22.library_of_exile.components.PlayerDataCapability;
import com.robertx22.library_of_exile.config.map_dimension.MapDimensionConfig;
import com.robertx22.library_of_exile.config.map_dimension.MapDimensionConfigDefaults;
import com.robertx22.library_of_exile.dimension.structure.MapStructure;
import com.robertx22.library_of_exile.main.ExileLog;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;

public abstract class MapDimensionInfo {

    public ResourceLocation dimensionId;
    public MapStructure<?> structure;
    public MapContentType contentType = MapContentType.SIDE_CONTENT;
    public List<MapStructure<?>> secondaryStructures = new ArrayList<>();
    public MapDimensionConfig config;
    public boolean markDataForClear = false;

    /**
     * Optional per-second "content starts in N seconds" action bar shown while a player is inside this
     * dimension's {@link MapEntryGrace} window, given the whole seconds left.
     * <p>
     * It has to be driven from the library's player tick rather than by the league itself: a league's own
     * per-second logic hangs off a spawner block entity, and those blocks ARE content - they're created by
     * ProcessMapChunks.spawnDataFromChunk, which is precisely what the grace window holds back. On a fresh
     * instance the block doesn't exist yet, so nothing league-side ticks to say anything.
     * <p>
     * Null (the default) means this dimension shows nothing, which is the right thing for one whose grace
     * only delays scenery the player wasn't waiting on anyway.
     */
    public IntFunction<Component> graceCountdownText = null;

    public MobValidator mobValidator = new MobValidator() {
        @Override
        public boolean isValidMob(LivingEntity en) {
            return true;
        }
    };


    public MapDimensionInfo(ResourceLocation dimensionId, MapStructure<?> structure, MapContentType contentType, List<MapStructure<?>> secondaryStructures, MobValidator mobValidator, MapDimensionConfigDefaults def) {
        this.dimensionId = dimensionId;
        this.structure = structure;
        this.contentType = contentType;
        this.secondaryStructures = secondaryStructures;
        this.mobValidator = mobValidator;
        this.config = MapDimensionConfig.register(this, def);
    }

    // clears the per instance data this dimension's own mod keeps. the instance COUNTER must survive this -
    // see resetInstanceCounter.
    public abstract void clearMapDataOnFolderWipe(MinecraftServer server);

    // restarts the MapStructureCounter, which hands out instance coordinates. only safe once the dimension's
    // region files are gone: an instance's mobs are never despawned by anything, and its chunks keep
    // LibChunkCap.mapGenData marked as already processed, so handing the same coordinates out again drops the
    // next player into the previous occupant's leftover (and previously levelled) mobs in a room that will
    // never generate fresh content. the wipe_world_data command cannot delete a live dimension's folder, so
    // it deliberately does NOT reset this - only the boot time folder wipe does.
    public abstract void resetInstanceCounter(MinecraftServer server);

    // clearMapDataOnFolderWipe can only reach the store its own mod owns, but a map instance's state is
    // spread across several: the league's own data, the library's LibMapData (relic stats) and mine and
    // slash's MapData (the map level). anything left behind is read back by whoever is later handed those
    // coordinates - a fresh map inheriting a level 100 MapData is how overleveled mobs appeared. mods
    // register here to clear what they own.
    private final List<Consumer<MinecraftServer>> onWipeListeners = new ArrayList<>();

    public void addOnWipeListener(Consumer<MinecraftServer> listener) {
        onWipeListeners.add(listener);
    }

    // wiping pulls every instance out from under whoever is standing in one, which then generates as some
    // other dungeon around them and floods the log. send them home first - same call the login handler
    // uses to evict players from maps wiped while they were offline.
    public final int evictPlayers(MinecraftServer server) {
        var level = server.getLevel(ResourceKey.create(Registries.DIMENSION, dimensionId));
        if (level == null) {
            return 0;
        }
        // copy: teleportHome moves players out of this level, mutating the list we'd be iterating
        List<ServerPlayer> inside = new ArrayList<>(level.players());

        for (ServerPlayer p : inside) {
            try {
                PlayerDataCapability.get(p).mapTeleports.teleportHome(p);
            } catch (Exception e) {
                ExileLog.get().warn("Failed to teleport " + p.getScoreboardName() + " out of " + dimensionId + ": " + e);
            }
        }
        return inside.size();
    }

    // chunksDeleted: whether this dimension's region files were actually removed. true only for the boot
    // time WipeDimensionFeature path, which is the one that earns a counter reset.
    public final void onFolderWipe(MinecraftServer server, boolean chunksDeleted) {
        int evicted = evictPlayers(server);

        clearMapDataOnFolderWipe(server);

        if (chunksDeleted) {
            resetInstanceCounter(server);
        }

        for (Consumer<MinecraftServer> listener : onWipeListeners) {
            try {
                listener.accept(server);
            } catch (Exception e) {
                // one mod's failure must not stop the rest from clearing theirs
                ExileLog.get().warn("A wipe listener for " + dimensionId + " failed: " + e);
                e.printStackTrace();
            }
        }
        MapConnectionsCap.get(server.overworld()).data.removeAllFor(dimensionId);

        ExileLog.get().log("Wiped saved map data for " + dimensionId + " (instance store, map connections, "
                + onWipeListeners.size() + " extra store(s)), sent " + evicted + " player(s) home. "
                + (chunksDeleted
                ? "Instance counter reset - the dimension folder was deleted, so those coordinates are free again."
                : "Instance counter preserved - the dimension folder still holds those chunks, so new maps keep"
                + " getting fresh coordinates instead of a previous run's leftovers."));
    }

    public boolean isInside(MapStructure struc, ServerLevel level, BlockPos pos) {

        if (!level.dimension().location().equals(dimensionId)) {
            return false;
        }
        if (!struc.isInside(level, pos)) {
            return false;
        }
        return true;
    }

    public boolean hasStructure(MapStructure struc) {
        if (struc.guid().equals(structure.guid())) {
            return true;
        }
        return secondaryStructures.stream().anyMatch(x -> x.guid().equals(struc.guid()));
    }


}
