package com.robertx22.library_of_exile.registry.register_info;

import net.minecraft.resources.ResourceLocation;

public class FromDatapackRegistration extends ExileRegistrationInfo {

    public ResourceLocation jsonLocation;

    public FromDatapackRegistration(ResourceLocation jsonLocation) {
        this.jsonLocation = jsonLocation;
    }
}
