package com.robertx22.library_of_exile.registry;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Database {

    private static HashMap<ExileRegistryType, ExileRegistryContainer> SERVER = new HashMap<>();
    private static HashMap<ExileRegistryType, ExileRegistryContainer> BACKUP = new HashMap<>();

    public static boolean areDatapacksLoaded(Level world) {
        return ExileRegistryType.getInRegisterOrder(SyncTime.ON_LOGIN)
                .stream()
                .allMatch(x -> Database.getRegistry(x)
                        .isRegistrationDone());
    }

    public static void backup() {
        BACKUP = new HashMap<>(SERVER);
    }

    public static void restoreBackup() {
        System.out.print("Restoring registry backup, this should never happen!");
        SERVER = new HashMap<>(BACKUP); // this doesnt appear to be EVER called.. But unsure if good idea to remove
    }

    public static void restoreFromBackupifEmpty() {
        if (SERVER.isEmpty()) {
            restoreBackup();
        }
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

        System.out.println("Starting Age of Exile Registry auto validation.");

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
            System.out.println("All Age of Exile registries appear valid.");
        } else {
            System.out.println(invalid.size() + " Age of Exile entries are INVALID!");
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
