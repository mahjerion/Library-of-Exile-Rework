package com.robertx22.library_of_exile.packets.defaults;

public class EntityPacket {

    /*
    public static final ResourceLocation ID = new ResourceLocation(Ref.MODID, "spawn_entity");

    public static Packet<?> createPacket(Entity entity) {
        FriendlyByteBuf buf = createBuffer();
        buf.writeVarInt(Registry.ENTITY_TYPE.getId(entity.getType()));
        buf.writeUUID(entity.getUUID());
        buf.writeVarInt(entity.getId());
        buf.writeDouble(entity.getX());
        buf.writeDouble(entity.getY());
        buf.writeDouble(entity.getZ());
        buf.writeByte(MathHelper.floor(entity.xRot * 256.0F / 360.0F));
        buf.writeByte(MathHelper.floor(entity.yRot * 256.0F / 360.0F));
        buf.writeFloat(entity.xRot);
        buf.writeFloat(entity.yRot);
        return ServerSidePacketRegistry.INSTANCE.toPacket(ID, buf);
    }

    private static FriendlyByteBuf createBuffer() {
        return new FriendlyByteBuf(Unpooled.buffer());
    }

*/

}

