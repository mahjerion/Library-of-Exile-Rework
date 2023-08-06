package com.robertx22.library_of_exile.vanilla_util;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.DimensionType;

public class VanillaRegistryHelper {


    public VanillaRegistryWrapper<Item> items() {
        return new VanillaRegistryWrapper<Item>(BuiltInRegistries.ITEM);
    }

    public VanillaRegistryWrapper<Block> blocks() {
        return new VanillaRegistryWrapper<Block>(BuiltInRegistries.BLOCK);
    }

    public VanillaRegistryWrapper<ParticleType<?>> particles() {
        return new VanillaRegistryWrapper<ParticleType<?>>(BuiltInRegistries.PARTICLE_TYPE);
    }

    public VanillaRegistryWrapper<Level> dimensions(Level level) {
        return new VanillaRegistryWrapper<Level>(level.registryAccess().registry(Registries.DIMENSION).get());
    }

    public VanillaRegistryWrapper<DimensionType> dimensionTypes(Level level) {
        return new VanillaRegistryWrapper<DimensionType>(level.registryAccess().registry(Registries.DIMENSION_TYPE).get());
    }


}
