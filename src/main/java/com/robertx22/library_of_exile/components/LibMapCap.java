package com.robertx22.library_of_exile.components;

import com.robertx22.library_of_exile.events.base.ExileEvents;
import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.registry.IAutoGson;
import com.robertx22.library_of_exile.utils.LoadSave;
import net.minecraft.core.BlockPos;
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

import java.util.Optional;

public class LibMapCap implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public Level world;

    public static final ResourceLocation RESOURCE = new ResourceLocation(Ref.MODID, "world_data");
    public static Capability<LibMapCap> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {
    });

    transient final LazyOptional<LibMapCap> supp = LazyOptional.of(() -> this);

    public LibMapCap(Level world) {
        this.world = world;
    }

    public static LibMapCap get(Level level) {
        return level.getServer().overworld().getCapability(INSTANCE).orElse(new LibMapCap(level));
    }

    // i'm a bit muddleheaded now but this should do.
    public static LibMapData getData(Level level, BlockPos pos) {
        ExileEvents.GrabLibMapData event = new ExileEvents.GrabLibMapData(level, pos);
        ExileEvents.GRAB_LIB_MAP_DATA.callEvents(event);

        if (event.data == null) {
            return new LibMapData();
        }
        return event.data;
    }

    public static Optional<LibMapData> ifData(Level level, BlockPos pos) {
        var data = getData(level, pos);
        return data != null ? Optional.of(data) : Optional.empty();
    }


    public LibMapDataSaver data = new LibMapDataSaver();

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
            nbt.putString("data", IAutoGson.GSON.toJson(data));
        } catch (Exception e) {
            e.printStackTrace();
        }


        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

        try {
            this.data = LoadSave.loadOrBlank(LibMapDataSaver.class, new LibMapDataSaver(), nbt, "data", new LibMapDataSaver());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
