package com.robertx22.library_of_exile.wrappers;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ExileText {

    public interface ITranslatable {
       
        public String getKey();
    }

    MutableComponent text;


    private ExileText(MutableComponent text) {
        this.text = text;
    }


    public static ExileText ofText(String s) {
        return new ExileText(Component.literal(s));
    }


    public static ExileText emptyLine() {
        return new ExileText(Component.literal(""));
    }

    public static ExileText newLine() {
        return new ExileText(Component.literal("\n"));
    }

    public static ExileText ofTranslate(ITranslatable s) {
        return new ExileText(Component.translatable(s.getKey()));
    }

    public static ExileText ofTranslate(ITranslatable s, Object... arg) {
        return new ExileText(Component.translatable(s.getKey(), arg));
    }

    public static ExileText ofTranslate(String s) {
        return new ExileText(Component.translatable(s));
    }

    public static ExileText ofTranslate(String s, Object... arg) {
        return new ExileText(Component.translatable(s, arg));
    }

    public ExileText append(String s) {
        text = text.append(s);
        return this;
    }

    public ExileText append(MutableComponent s) {
        text = text.append(s);
        return this;
    }

    public ExileText format(ChatFormatting color) {
        text = text.withStyle(color);
        return this;
    }

    public MutableComponent get() {
        return text;
    }
}

