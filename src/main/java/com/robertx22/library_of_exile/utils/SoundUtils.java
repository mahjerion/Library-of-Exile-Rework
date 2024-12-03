package com.robertx22.library_of_exile.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;


public class SoundUtils {

    public static void playSound(Entity en, SavedSound event) {
        playSound(en, event.sound, event.volume, event.pitch);
    }

    public static void playSound(Level world, BlockPos pos, SavedSound event) {
        playSound(world, pos, event.sound, event.volume, event.pitch);
    }

    public static void playSound(Entity en, SoundEvent event) {
        playSound(en, event, 1, 1);
    }

    public static void playSound(Level world, BlockPos pos, SoundEvent event) {
        playSound(world, pos, event, 1, 1);
    }

    public static void playSound(Entity en, SoundEvent event, float volume, float pitch) {
        if (!en.level().isClientSide) {
            en.level().playSound(null, en.getX(), en.getY(), en.getZ(), event, SoundSource.PLAYERS, volume, pitch);
        } else {
            en.level().playLocalSound(en.getX(), en.getY(), en.getZ(), event, SoundSource.PLAYERS, volume, pitch, true);
        }
    }

    public static void playSound(Level world, BlockPos pos, SoundEvent event, float volume, float pitch) {
        playSound(world, pos, event, SoundSource.PLAYERS, volume, pitch);
    }

    public static void playSound(net.minecraft.world.level.Level world, BlockPos pos, SoundEvent event, SoundSource cat, float volume, float pitch) {
        if (!world.isClientSide) {
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), event, cat, volume, pitch);
        } else {
            world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), event, cat, volume, pitch, true);
        }
    }

    public static void ding(Level world, BlockPos pos) {
        SoundUtils.playSound(world, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, 1, 1);
    }

}
