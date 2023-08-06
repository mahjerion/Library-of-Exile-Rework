package com.robertx22.library_of_exile.utils;

import net.minecraft.sounds.SoundEvent;

public class SavedSound {

    public SoundEvent sound;
    public float pitch = 1;
    public float volume = 1;

    public SavedSound(SoundEvent sound, float pitch, float volume) {
        this.sound = sound;
        this.pitch = pitch;
        this.volume = volume;
    }

    public SavedSound(SoundEvent sound, float pitch) {
        this.sound = sound;
        this.pitch = pitch;
    }

    public SavedSound(SoundEvent sound) {
        this.sound = sound;
    }
}
