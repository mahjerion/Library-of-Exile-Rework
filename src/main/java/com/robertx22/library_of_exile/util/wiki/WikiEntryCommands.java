package com.robertx22.library_of_exile.util.wiki;

import com.mojang.brigadier.CommandDispatcher;
import com.robertx22.library_of_exile.command_wrapper.CommandBuilder;
import com.robertx22.library_of_exile.command_wrapper.PermWrapper;
import com.robertx22.library_of_exile.command_wrapper.PlayerWrapper;
import com.robertx22.library_of_exile.command_wrapper.RegistryTypeWrapper;
import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.registry.Database;
import com.robertx22.library_of_exile.registry.ExileRegistry;
import com.robertx22.library_of_exile.registry.ExileRegistryContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public class WikiEntryCommands {

    public static void init(CommandDispatcher dis) {

        CommandBuilder.of(Ref.MODID, dis, x -> {
            PlayerWrapper PLAYER = new PlayerWrapper();
            var REG = new RegistryTypeWrapper();

            x.addLiteral("generate", PermWrapper.OP);
            x.addLiteral("wiki_entries", PermWrapper.OP);

            x.addArg(PLAYER);
            x.addArg(REG);

            x.action(e -> {
                var en = PLAYER.get(e);
                var reg = REG.get(e);

                ExileRegistryContainer<?> db = Database.getRegistry(reg);

                String text = "";

                for (ExileRegistry<?> o : db.getList()) {
                    if (o instanceof iWikiEntry wiki && wiki.getWikiEntry().use) {
                        text += " - " + "'" + o.GUID() + "'" + " - " + wiki.getWikiEntry().text + "\n";
                    }
                }
                en.sendSystemMessage(Component.literal(ChatFormatting.YELLOW + "" + ChatFormatting.BOLD + "Click Here to Copy Text.")
                        .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, text))));

            });

        }, "");
    }
}
