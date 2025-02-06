package com.robertx22.library_of_exile.components;

import com.robertx22.library_of_exile.dimension.teleport.SavedPlayerMapTeleports;
import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.utils.LoadSave;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerDataCapability implements ICap {


    public static final ResourceLocation RESOURCE = new ResourceLocation(Ref.MODID, "player");
    public static Capability<PlayerDataCapability> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static PlayerDataCapability get(Player p) {
        return p.getCapability(INSTANCE).orElse(null);
    }

    transient final LazyOptional<PlayerDataCapability> supp = LazyOptional.of(() -> this);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == INSTANCE) {
            return supp.cast();
        }
        return LazyOptional.empty();

    }

    private static final String MAP_TPS = "map_tps";


    transient Player player;

    public SavedPlayerMapTeleports mapTeleports = new SavedPlayerMapTeleports();

    public PlayerDataCapability(Player player) {
        this.player = player;
    }


    public DelayedTeleportData delayedTeleportData = null;

    @Override
    public CompoundTag serializeNBT() {

        CompoundTag nbt = new CompoundTag();

        try {
            if (mapTeleports != null) {
                LoadSave.Save(mapTeleports, nbt, MAP_TPS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

        try {
            this.mapTeleports = LoadSave.Load(SavedPlayerMapTeleports.class, new SavedPlayerMapTeleports(), nbt, MAP_TPS);
            if (mapTeleports == null) {
                mapTeleports = new SavedPlayerMapTeleports();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void syncToClient(Player player) {
        // dont sync backpacks to client
        //  Packets.sendToClient(player, new SyncPlayerCapToClient(player, this.getCapIdForSyncing()));
    }


    @Override
    public String getCapIdForSyncing() {
        return "player_data";
    }

}
