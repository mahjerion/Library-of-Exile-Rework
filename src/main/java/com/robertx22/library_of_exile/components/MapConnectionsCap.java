package com.robertx22.library_of_exile.components;

import com.google.gson.JsonSyntaxException;
import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.utils.LoadSave;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MapConnectionsCap implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public Level world;

    public static final ResourceLocation RESOURCE = new ResourceLocation(Ref.MODID, "map_connections");
    public static Capability<MapConnectionsCap> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {
    });

    transient final LazyOptional<MapConnectionsCap> supp = LazyOptional.of(() -> this);

    public MapConnectionsCap(Level world) {
        this.world = world;
    }

    public static MapConnectionsCap get(Level entity) {
        var overworld = entity.getServer().overworld();
        return overworld.getCapability(INSTANCE).orElse(new MapConnectionsCap(overworld));
    }

    public AllMapConnectionData data = new AllMapConnectionData();

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == INSTANCE) {
            return supp.cast();
        }
        return LazyOptional.empty();

    }

    @Override
    public CompoundTag serializeNBT() {
        var nbt = new CompoundTag();

        try {
            LoadSave.Save(data, nbt, "data");
        } catch (Exception e) {
            e.printStackTrace();
        }


        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

        try {

            this.data = LoadSave.loadOrBlank(AllMapConnectionData.class, new AllMapConnectionData(), nbt, "data", new AllMapConnectionData());


        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

    }
}
