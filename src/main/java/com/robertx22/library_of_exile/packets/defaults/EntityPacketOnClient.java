package com.robertx22.library_of_exile.packets.defaults;

public class EntityPacketOnClient {
    /*
    @Environment(EnvType.CLIENT)
    public static void onPacket(Context context, FriendlyByteBuf byteBuf) {
        EntityType<?> type = Registry.ENTITY_TYPE.byId(byteBuf.readVarInt());
        UUID entityUUID = byteBuf.readUUID();
        int entityID = byteBuf.readVarInt();
        double x = byteBuf.readDouble();
        double y = byteBuf.readDouble();
        double z = byteBuf.readDouble();
        float pitch = (byteBuf.readByte() * 360) / 256.0F;
        float yaw = (byteBuf.readByte() * 360) / 256.0F;
        context.getTaskQueue()
            .execute(() -> {
                ClientLevel world = Minecraft.getInstance().level;
                Entity entity = type.create(world);
                if (entity != null) {
                    entity.setPos(x, y, z);
                    entity.setPacketCoordinates(x, y, z);
                    entity.xRot = pitch;
                    entity.yRot = yaw;
                    entity.setId(entityID);
                    entity.setUUID(entityUUID);
                    //entity.setVelocity(velocityX, velocityY, velocityZ);
                    world.putNonPlayerEntity(entityID, entity);
                }
            });
    }

     */
}
