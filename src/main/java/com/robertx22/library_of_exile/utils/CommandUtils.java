package com.robertx22.library_of_exile.utils;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class CommandUtils {

    public static void execute(Player player, String command) {

        CommandSourceStack source = getCommandSource(player);

        player.getServer()
                .getCommands()
                .performCommand(player.getServer().getCommands().getDispatcher().parse(command, source), command);
    }

    public static CommandSourceStack getCommandSource(Entity entity) {

        return entity.createCommandSourceStack();
        
        /*

        return new CommandSource(
                // this doesnt send messages to spam server
                CommandSource.NULL,
                entity.position(),
                entity.getRotationVector(),
                entity.level() instanceof ServerLevel ? (ServerLevel) entity.level() : null,
                4,
                entity.getName()
                        .getString(),
                entity.getDisplayName(),
                entity.level().getServer(),
                entity);
                
         */
    }
}
