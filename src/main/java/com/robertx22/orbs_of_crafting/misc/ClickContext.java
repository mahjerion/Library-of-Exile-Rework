package com.robertx22.orbs_of_crafting.misc;

import com.robertx22.library_of_exile.main.ExileLog;
import com.robertx22.library_of_exile.utils.SoundUtils;
import com.robertx22.orbs_of_crafting.register.Requirements;
import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvents;
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
 * <p>
 * Every click feature modifies exactly one item, so a slot holding more than one is refused outright
 * - see {@link #refuseIfStacked()}.
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

    /**
     * Sophisticated Backpacks/Storage (and this mod's own backpack, when a tab's stack_multiplier is
     * raised) let otherwise unstackable items share a slot.
     */
    public boolean isTargetStacked() {
        return target.getCount() > 1;
    }

    /**
     * Every click feature modifies exactly one item, and the crafting pipeline carries the stack
     * count straight into its result, so a stack of N would come back as N modified items for the
     * price of one currency - a duplication bug.
     * <p>
     * Refuses rather than splitting one item off the stack: the split would have to be written back
     * into a third party container, and there is no way to prove from inside this event that the
     * write survives that container's own doClick.
     * <p>
     * Call this once a feature has decided the click is meant for it, and before anything is
     * consumed. Returns true when the click must be abandoned.
     */
    public boolean refuseIfStacked() {
        if (!isTargetStacked()) {
            return false;
        }
        SoundUtils.playSound(player.level(), player.blockPosition(), SoundEvents.VILLAGER_NO, 1, 1);
        player.sendSystemMessage(Requirements.INSTANCE.IS_SINGLE_ITEM.get().getDescWithParams().withStyle(ChatFormatting.RED));
        return true;
    }

    /** consumes one target item and puts the modified item in its place */
    public void replaceTarget(ItemStack result) {
        if (refusedStackedWrite("replaceTarget")) {
            return;
        }
        slot.set(result.copyWithCount(1));
    }

    /** writes a target that was modified in place back into its slot */
    public void updateTarget(ItemStack modified) {
        if (refusedStackedWrite("updateTarget")) {
            return;
        }
        slot.set(modified.copyWithCount(1));
    }

    public void consumeTarget(int amount) {
        if (refusedStackedWrite("consumeTarget")) {
            return;
        }
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

    // fails closed. a feature that forgets refuseIfStacked() gets a no-op and a log line instead of
    // handing the player a whole stack's worth of modified items
    private boolean refusedStackedWrite(String method) {
        if (!isTargetStacked()) {
            return false;
        }
        ExileLog.get().warn("A click feature called {} on a stack of {} {} - refused. It should have called refuseIfStacked() first.",
                method, target.getCount(), target.getItem());
        return true;
    }

    private static ItemStack shrunk(ItemStack stack, int amount) {
        ItemStack copy = stack.copy();
        copy.shrink(amount);
        return copy.isEmpty() ? ItemStack.EMPTY : copy;
    }
}
