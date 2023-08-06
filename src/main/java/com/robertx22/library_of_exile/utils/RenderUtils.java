package com.robertx22.library_of_exile.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class RenderUtils {

    public static void render16Icon(GuiGraphics matrix, ResourceLocation tex, int x, int y) {
        Minecraft.getInstance()
                .getTextureManager()
                .bindForSetup(tex);


        matrix.blit(tex, x, y, 0, 0, 16, 16, 16, 16);
    }

    public static void renderStack(GuiGraphics gui, ItemStack stack, int x, int y) {
        Minecraft mc = Minecraft.getInstance();
        RenderSystem.enableDepthTest();

        
        gui.pose().pushPose();

        gui.pose().translate(x, y, 0);

        //BakedModel bakedmodel = mc.getItemRenderer().getModel(stack, LibClientOnly.getPlayer().level(), (LivingEntity) null, 1);

        gui.renderItem(stack, x, y);

        // mc.getItemRenderer().render(stack, ItemDisplayContext.GROUND, false, gui.pose(), mc.renderBuffers().bufferSource(), 100, OverlayTexture.NO_OVERLAY, bakedmodel);

/*
        mc.getItemRenderer()
                .renderAndDecorateFakeItem(stack, x, y);
        mc.getItemRenderer()
                .renderGuiItemDecorations(mc.font, stack, x, y);

 */

        RenderSystem.disableDepthTest();

        gui.pose().popPose();
    }
}