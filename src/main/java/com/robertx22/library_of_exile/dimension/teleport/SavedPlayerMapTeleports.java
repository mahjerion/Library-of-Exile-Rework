package com.robertx22.library_of_exile.dimension.teleport;

import com.robertx22.library_of_exile.components.PlayerDataCapability;
import com.robertx22.library_of_exile.dimension.MapDimensions;
import com.robertx22.library_of_exile.utils.TeleportUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class SavedPlayerMapTeleports {


    // the dimension the player came from, the dim must be not one of the 'map' dimensions
    public SavedTeleportPos home = new SavedTeleportPos();

    // the last tp
    public List<SavedTeleportPos> last = new ArrayList<>();

    // game time of the last teleport INTO a map dimension from outside it. read by MapEntryGrace to hold
    // content back while a slow client is still loading the chunks it was just dropped into. saved rather
    // than transient so a relog straight back into an instance is still covered. teleports that stay inside
    // the dimension - the arena pads, the tp to boss button - deliberately leave this alone, see teleportToMap.
    public long lastMapEnterTime = 0;


    SavedTeleportPos getLast() {
        return last.get(last.size() - 1);
    }

    void deleteLast() {
        if (!last.isEmpty()) {
            last.remove(last.size() - 1);
        }
    }

    // teleports to maps
    public void entranceTeleportLogic(Player p, ResourceLocation to, BlockPos topos) {
        ResourceLocation from = p.level().dimensionTypeId().location();
        teleportToMap(p, from, to, topos);
    }

    // if in map, teleports to last map teleport point, or if theres no more points, tps back home
    // points are deleted when used or when you teleport from home again
    public void exitTeleportLogic(Player p) {

        ResourceLocation from = p.level().dimensionTypeId().location();

        boolean fromMap = MapDimensions.isMap(from);

        if (fromMap) {
            if (this.last.isEmpty()) {
                teleportHome(p);
            } else {
                teleportToLast(p);
            }
        }
    }


    public void teleportHome(Player p) {
        var dim = home.getDimensionId();
        if (p.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, dim)) == null) {
            dim = p.getServer().overworld().dimension().location();
        }
        teleport(p, dim, home.getPos());
    }

    public void teleportToLast(Player p) {
        teleport(p, getLast().getDimensionId(), getLast().getPos());
        deleteLast();
    }

    public void teleportToMap(Player p, ResourceLocation from, ResourceLocation to, BlockPos topos) {
        boolean fromMap = MapDimensions.isMap(from);

        if (!fromMap) {
            home.setFrom(p);
            last = new ArrayList<>();
        } else {
            var data = new SavedTeleportPos();
            data.setFrom(p);
            last.add(data);
        }
        // every league enters its dimension through here (entranceTeleportLogic), so this is the one place
        // that has to stamp the arrival - but only a genuine arrival. the arena / uber / reward room pads
        // (CustomSpawnTpBlock) and the tp to boss button also come through here, with from == to, and
        // restamping there handed the player a fresh 10 seconds of empty arena plus a second countdown. the
        // grace covers a client loading a dimension it was just dropped into; by the time you reach the
        // arena that has long since happened.
        //
        // the from.equals(to) half keeps a real map -> different map entry covered, for a league whose
        // entrance can be reached from inside another league's dimension.
        //
        // the decision is made here, but the stamp itself happens on ARRIVAL - see
        // DelayedTeleportData.stampMapEnterOnArrival. The teleport now waits for the destination
        // chunks, which can take seconds, and stamping up front would spend that part of the grace
        // while the player is still standing in the dimension they came from.
        boolean stampOnArrival = !fromMap || !from.equals(to);

        teleport(p, to, topos);

        var cap = PlayerDataCapability.get(p);
        var delayed = cap == null ? null : cap.delayedTeleportData;
        if (delayed != null) {
            delayed.stampMapEnterOnArrival = stampOnArrival;
        } else if (stampOnArrival) {
            // teleport() always sets one, but never silently lose the grace if that ever changes
            this.lastMapEnterTime = p.level().getGameTime();
        }
    }

    private void teleport(Player p, ResourceLocation to, BlockPos pos) {
        TeleportUtils.teleport((ServerPlayer) p, pos, to);
    }

}
