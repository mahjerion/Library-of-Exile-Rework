package com.robertx22.library_of_exile.gui;

import com.robertx22.library_of_exile.main.Ref;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class HelpButton extends ImageButton {

    static ResourceLocation ID = new ResourceLocation(Ref.MODID, "textures/gui/spell_help.png");

    public HelpButton(int x, int y) {
        super(x, y, 20, 20, 0, 0, 1, ID,
                action -> {

                });


    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        setTooltip(Tooltip.create(Component.literal("test button text")));
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }


/*
    public boolean isInside(int x, int y) {
        return GuiUtils.isInRect(this.x, this.y, 20, 20, x, y);
    }

 */

    // todo
    /*
    @Override
    public void renderToolTip(GuiGraphics matrices, int mouseX, int mouseY) {
        if (isInside(mouseX, mouseY)) {
            GuiUtils.renderTooltip(matrices, tooltip, mouseX, mouseY);
        }
    }

     */
}