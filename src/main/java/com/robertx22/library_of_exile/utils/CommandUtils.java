package com.robertx22.library_of_exile.utils;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class CommandUtils {

    public static void execute(Player player, String command) {
        CommandSourceStack source = getCommandSource(player);
        player.getServer().getCommands().performPrefixedCommand(source, command);

    }

    public static CommandSourceStack getCommandSource(Entity entity) {
        return createCommandSourceStack(entity);
    }

    public static CommandSourceStack createCommandSourceStack(Entity en) {
        return new CommandSourceStack(en, en.position(), en.getRotationVector(),
                en.level() instanceof ServerLevel ? (ServerLevel) en.level() : null,
                100, en.getName().getString(), en.getDisplayName(), en.level().getServer(), en);
    }
}
