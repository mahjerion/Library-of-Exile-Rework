package com.robertx22.library_of_exile.components;

import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.utils.LoadSave;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LibChunkCap implements ICap {


    public static final ResourceLocation RESOURCE = new ResourceLocation(Ref.MODID, "chunk_data");
    public static Capability<LibChunkCap> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {
    });

    transient final LazyOptional<LibChunkCap> supp = LazyOptional.of(() -> this);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == INSTANCE) {
            return supp.cast();
        }
        return LazyOptional.empty();

    }


    transient LevelChunk chunk;

    public MapChunkData mapGenData = new MapChunkData();

    public LibChunkCap(LevelChunk chunk) {
        this.chunk = chunk;
    }


    @Override
    public CompoundTag serializeNBT() {

        CompoundTag nbt = new CompoundTag();
        try {
            LoadSave.Save(mapGenData, nbt, "map");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        try {
            this.mapGenData = LoadSave.loadOrBlank(MapChunkData.class, new MapChunkData(), nbt, "map", new MapChunkData());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getCapIdForSyncing() {
        return "chunk_data";
    }

}
