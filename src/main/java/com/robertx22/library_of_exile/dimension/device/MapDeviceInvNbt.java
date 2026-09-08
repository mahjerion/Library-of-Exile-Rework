package com.robertx22.library_of_exile.dimension.device;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

import static com.robertx22.library_of_exile.dimension.device.IMapDeviceBlockEntity.MAP_SLOT;
import static com.robertx22.library_of_exile.dimension.device.IMapDeviceBlockEntity.RELIC_SLOTS;
import static com.robertx22.library_of_exile.dimension.device.IMapDeviceBlockEntity.RELIC_SLOT_START;

/**
 * NBT for a device inventory that remembers which slot each stack sat in.
 * <p>
 * Vanilla's {@link SimpleContainer#createTag()} / {@link SimpleContainer#fromTag(ListTag)} pair drops the
 * slot index and re-adds stacks into the first free slots on load, so a relic in a relic slot with no map
 * slotted came back in the map slot after a relog. This writes a {@code Slot} byte per stack (the same
 * shape as {@code ContainerHelper}) and puts each stack back exactly where it was.
 */
public class MapDeviceInvNbt {

    private static final String SLOT = "Slot";

    public static ListTag save(SimpleContainer inv) {
        ListTag list = new ListTag();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                CompoundTag tag = new CompoundTag();
                tag.putByte(SLOT, (byte) i);
                stack.save(tag);
                list.add(tag);
            }
        }
        return list;
    }

    /**
     * Entries carrying a slot index go straight back to it. Entries without one were written by the older
     * index-less save and are placed by kind: a map into the map slot, anything else into the first free
     * relic slot, so a relic that had drifted into the map slot moves back where it belongs. Runs once per
     * device - the next save writes indices.
     *
     * @param isMap whether a stack is a map this device accepts, see {@link IMapDeviceBlockEntity#acceptsMapItem}
     */
    public static void load(SimpleContainer inv, ListTag list, Predicate<ItemStack> isMap) {
        inv.clearContent();

        for (int i = 0; i < list.size(); i++) {
            CompoundTag tag = list.getCompound(i);
            ItemStack stack = ItemStack.of(tag);
            if (stack.isEmpty()) {
                continue;
            }
            int slot;
            if (tag.contains(SLOT)) {
                slot = tag.getByte(SLOT) & 255;
                if (slot < 0 || slot >= inv.getContainerSize()) {
                    continue;
                }
            } else {
                slot = legacySlotFor(inv, stack, isMap);
                if (slot < 0) {
                    continue;
                }
            }
            inv.setItem(slot, stack);
        }
    }

    private static int legacySlotFor(SimpleContainer inv, ItemStack stack, Predicate<ItemStack> isMap) {
        if (isMap.test(stack) && inv.getItem(MAP_SLOT).isEmpty()) {
            return MAP_SLOT;
        }
        for (int s = RELIC_SLOT_START; s < RELIC_SLOT_START + RELIC_SLOTS && s < inv.getContainerSize(); s++) {
            if (inv.getItem(s).isEmpty()) {
                return s;
            }
        }
        for (int s = 0; s < inv.getContainerSize(); s++) {
            if (inv.getItem(s).isEmpty()) {
                return s;
            }
        }
        return -1;
    }
}
