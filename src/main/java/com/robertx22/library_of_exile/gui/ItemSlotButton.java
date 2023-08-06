package com.robertx22.library_of_exile.gui;


import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.utils.RenderUtils;
import com.robertx22.library_of_exile.utils.TextUTIL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class ItemSlotButton extends ImageButton {

    public static int xSize = 16;
    public static int ySize = 16;

    static ResourceLocation buttonLoc = new ResourceLocation(Ref.MODID, "");

    static ResourceLocation fancyBorderLoc = new ResourceLocation(Ref.MODID, "textures/gui/pretty_icon_border.png");
    static int FX = 20;
    static int FY = 20;
    ItemStack stack;
    Minecraft mc = Minecraft.getInstance();

    public boolean renderFancyBorder = false;

    public ItemSlotButton(ItemStack stack, int xPos, int yPos) {
        this(stack, xPos, yPos, (button) -> {
        });
        this.stack = stack;

    }

    public ItemSlotButton(ItemStack stack, int xPos, int yPos, Button.OnPress onclick) {
        super(xPos + 1, yPos + 1, xSize, ySize, 0, 0, ySize + 1, buttonLoc, onclick);
        this.stack = stack;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        // todo would this work?
        setTooltip(Tooltip.create(TextUTIL.mergeList(stack.getTooltipLines(mc.player, TooltipFlag.NORMAL))));

        RenderUtils.renderStack(pGuiGraphics, stack, this.getX(), this.getY());

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    /*
    @Override
    public void renderButton(MatrixStack matrix, int x, int y, float ticks) {

        if (renderFancyBorder) {
            mc.getTextureManager()
                    .bind(fancyBorderLoc);
            blit(matrix, this.x - 2, this.y - 2, 0, 0, FX, FY);
        }

        RenderUtils.renderStack(stack, this.x, this.y);

    }

     */


    /*
    @Override
    public void renderToolTip(GuiGraphics matrix, int x, int y) {
        if (!stack.isEmpty()) {
            if (isInside(x, y)) {
                List<ITextComponent> tooltip = new ArrayList<>();
                tooltip.addAll(stack.getTooltipLines(mc.player, ITooltipFlag.TooltipFlags.NORMAL));
                GuiUtils.renderTooltip(matrix, tooltip, x, y);
            }
        }
    }

     */

    /*
    public boolean isInside(int x, int y) {
        return GuiUtils.isInRect(this.x, this.y, xSize, ySize, x, y);
    }


     */
}

