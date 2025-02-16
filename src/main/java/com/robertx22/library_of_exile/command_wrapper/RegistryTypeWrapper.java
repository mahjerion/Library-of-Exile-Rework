package com.robertx22.library_of_exile.command_wrapper;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.robertx22.library_of_exile.registry.Database;
import com.robertx22.library_of_exile.registry.ExileRegistryType;

import java.util.stream.Collectors;

import static net.minecraft.commands.Commands.argument;

public class RegistryTypeWrapper extends ArgumentWrapper<ExileRegistryType> {

    public RegistryTypeWrapper() {
        super("registry_type");
        this.suggests(() -> Database.getAllRegistries().stream().map(x -> x.getType().id).collect(Collectors.toList()));
    }


    @Override
    public ExileRegistryType getter(CommandContext ctx) {
        var id = StringArgumentType.getString(ctx, argName);
        return Database.getAllRegistries().stream().filter(x -> x.getType().id.equals(id)).findAny().get().getType();
    }


    @Override
    public String id() {
        return "registry_type";
    }

    @Override
    public RequiredArgumentBuilder getType() {
        var b = argument(argName, StringArgumentType.string());
        return b;
    }
}
