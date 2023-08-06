package com.robertx22.library_of_exile.components;

public class TestComp {
    /*
    public static Capability<TestImpl> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(EntityInfoComponent.DefaultImpl.class);

    }

    public static LazyOptional<TestImpl> get(LivingEntity en) {
        return en.getCapability(INSTANCE);
    }

    @Mod.EventBusSubscriber
    public static class EventHandler {
        @SubscribeEvent
        public static void onEntityConstruct(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof LivingEntity) {
                event.addCapability(new ResourceLocation("test"), new EntityInfoComponent.Provider((LivingEntity) event.getObject()));
            }
        }
    }

    public class TestImpl implements ICapabilitySerializable<CompoundTag> {

        LivingEntity en;

        public TestImpl(LivingEntity en) {
            this.en = en;
        }

        final LazyOptional<TestImpl> supp = LazyOptional.of(() -> this);

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (cap == INSTANCE) {
                return supp.cast();
            }
            return LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return null;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {

        }
    }

     */
}
