package com.robertx22.library_of_exile.events.base;

// todo load order makes this complicated
public abstract class RegisterRegistriesEvent extends ExileEvent {

    public abstract void registerContainers();

    public abstract void addToDataGeneration();

    public abstract void addToRegistry();
}
