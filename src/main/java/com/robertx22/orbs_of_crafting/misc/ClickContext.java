package com.robertx22.orbs_of_crafting.misc;

import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemStackedOnOtherEvent;

/**
 * One side of a currency click. The currency is always the carried (cursor) stack and the item it
 * gets applied to is always the stack in the clicked slot - the reverse is not a way to use currency.
 * <p>
 * Writes always go through {@link Slot#set} / {@link SlotAccess#set} instead of mutating the stacks
 * in place. Vanilla slots hand out the live stack, but slots backed by an IItemHandler (Sophisticated
 * Backpacks/Storage, and most modded containers) route it through their own handler, so an in place
 * edit can end up neither saved nor synced.
 */
public class ClickContext {

    public final Player player;
    /** the stack being modified, always the one in the clicked slot */
    public final ItemStack target;
    /** the stack being consumed, always the one on the cursor */
    public final ItemStack currency;
    public final Slot slot;

    private final SlotAccess carried;

    private ClickContext(Player player, ItemStack target, ItemStack currency, Slot slot, SlotAccess carried) {
        this.player = player;
        this.target = target;
        this.currency = currency;
        this.slot = slot;
        this.carried = carried;
    }

    /**
     * Deliberately ignores {@link ItemStackedOnOtherEvent#getCarriedItem()} and
     * {@link ItemStackedOnOtherEvent#getStackedOnItem()}, because callers disagree on what they mean.
     * Forge's own AbstractContainerMenu patch calls
     * {@code onItemStackedOn(slot.getItem(), getCarried(), ..)} while the parameters are declared
     * {@code (carriedItem, stackedOnItem, ..)}, so in a vanilla container those two accessors are
     * inverted from their names. Sophisticated Backpacks/Storage reimplement doClick and pass the
     * arguments the way the names read, so the same accessors mean the opposite thing there.
     * <p>
     * The slot and the carried slot access are passed identically by both, so the roles are taken
     * from those instead and come out right in either container.
     */
    public static ClickContext of(ItemStackedOnOtherEvent e) {
        return new ClickContext(e.getPlayer(), e.getSlot().getItem(), e.getCarriedSlotAccess().get(), e.getSlot(), e.getCarriedSlotAccess());
    }

    public boolean isValid() {
        return !target.isEmpty() && !currency.isEmpty();
    }

    /** consumes one target item and puts the modified item in its place */
    public void replaceTarget(ItemStack result) {
        if (target.getCount() <= 1) {
            slot.set(result);
        } else {
            slot.set(target.copyWithCount(target.getCount() - 1));
            giveToPlayer(result);
        }
    }

    /** writes a target that was modified in place back into its slot */
    public void updateTarget(ItemStack modified) {
        slot.set(modified);
    }

    public void consumeTarget(int amount) {
        slot.set(shrunk(target, amount));
    }

    /** consumes part of the currency stack, leaving the rest of it on the cursor */
    public void consumeCurrency(int amount) {
        carried.set(shrunk(currency, amount));
    }

    public void giveToPlayer(ItemStack stack) {
        if (!stack.isEmpty()) {
            player.getInventory().placeItemBackInInventory(stack);
        }
    }

    private static ItemStack shrunk(ItemStack stack, int amount) {
        ItemStack copy = stack.copy();
        copy.shrink(amount);
        return copy.isEmpty() ? ItemStack.EMPTY : copy;
    }
}
