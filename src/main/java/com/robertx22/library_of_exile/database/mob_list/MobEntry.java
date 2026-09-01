package com.robertx22.library_of_exile.database.mob_list;

import com.robertx22.library_of_exile.registry.IWeighted;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

public class MobEntry implements IWeighted {

    public int weight;
    public String mob_id;

    public MobEntry(int weight, EntityType mob) {
        this.weight = weight;
        this.mob_id = ForgeRegistries.ENTITY_TYPES.getKey(mob).toString();
    }

    /**
     * By id, for a mob this library can't hold a reference to.
     * <p>
     * The lists here are the library's, but the mobs on them belong to whoever is installed - the
     * addon mods, or in a modpack a dozen third party ones. {@link #getType} answers null for an id
     * nothing registered, which {@link MobList#getRandomMob} filters out, so naming a mob that isn't
     * present is safe rather than a crash waiting for the right roll.
     */
    public MobEntry(int weight, String mobId) {
        this.weight = weight;
        this.mob_id = mobId;
    }

    public EntityType getType() {
        return ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(mob_id));
    }

    @Override
    public int Weight() {
        return weight;
    }
}
