package com.robertx22.library_of_exile.registry;

import com.robertx22.library_of_exile.main.ExileLog;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Database {

    private static HashMap<ExileRegistryType, ExileRegistryContainer> SERVER = new HashMap<>();
    //  private static HashMap<ExileRegistryType, ExileRegistryContainer> BACKUP = new HashMap<>();

    public static boolean areDatapacksLoaded(Level world) {
        return ExileRegistryType.getInRegisterOrder(SyncTime.ON_LOGIN)
                .stream()
                .allMatch(x -> Database.getRegistry(x)
                        .isRegistrationDone());
    }


    public static List<ExileRegistryContainer> getAllRegistries() {
        return new ArrayList<>(SERVER.values());
    }

    public static ExileRegistryContainer getRegistry(ExileRegistryType type) {
        return SERVER.get(type);
    }

    public static ExileRegistry get(ExileRegistryType type, String guid) {
        return getRegistry(type).get(guid);

    }

    public static void sendPacketsToClient(ServerPlayer player, SyncTime sync) {

        List<ExileRegistryType> list = ExileRegistryType.getInRegisterOrder(sync);

        list.forEach(x -> getRegistry(x).sendUpdatePacket(player));
    }

    public static void checkGuidValidity() {

        SERVER.values()
                .forEach(c -> c.getAllIncludingSeriazable()
                        .forEach(x -> {
                            ExileRegistry entry = (ExileRegistry) x;
                            if (!entry.isGuidFormattedCorrectly()) {
                                throw new RuntimeException(entry.getInvalidGuidMessage());
                            }
                        }));

    }

    public static void unregisterInvalidEntries() {

        //System.out.println("Starting Mine and Slash Registry auto validation.");

        List<ExileRegistry> invalid = new ArrayList<>();

        SERVER.values()
                .forEach(c -> c.getList()
                        .forEach(x -> {
                            ExileRegistry entry = (ExileRegistry) x;
                            if (!entry.isRegistryEntryValid()) {
                                invalid.add(entry);
                            }
                        }));

        invalid.forEach(x -> x.unregisterDueToInvalidity());

        if (invalid.isEmpty()) {
            ExileLog.get().debug("All Mine and Slash registries appear valid.");
        } else {
            ExileLog.get().warn(invalid.size() + " Mine and Slash entries are INVALID!");
        }

    }

    public static void addRegistry(ExileRegistryContainer cont) {
        SERVER.put(cont.getType(), cont);
    }

    public static void initRegistries() {
        SERVER = new HashMap<>();

        ExileRegistryType.init();
    }

}
