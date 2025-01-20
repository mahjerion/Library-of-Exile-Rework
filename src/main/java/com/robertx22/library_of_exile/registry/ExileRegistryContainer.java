package com.robertx22.library_of_exile.registry;

import com.google.common.base.Preconditions;
import com.robertx22.library_of_exile.events.base.ExileEvents;
import com.robertx22.library_of_exile.main.ExileLog;
import com.robertx22.library_of_exile.main.LibraryOfExile;
import com.robertx22.library_of_exile.main.Packets;
import com.robertx22.library_of_exile.packets.registry.EfficientRegistryPacket;
import com.robertx22.library_of_exile.registry.register_info.ExileRegistrationInfo;
import com.robertx22.library_of_exile.registry.register_info.RegistrationInfoData;
import com.robertx22.library_of_exile.utils.RandomUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ExileRegistryContainer<C extends ExileRegistry> {

    private List<String> registersErrorsAlertedFor = new ArrayList<>();
    private List<String> accessorErrosAletedFor = new ArrayList<>();
    private List<String> emptyRegistries = new ArrayList<>();

    public HashMap<String, RegistrationInfoData> registrationInfo = new HashMap<>();

    private boolean dataPacksAreRegistered = true;

    boolean isDatapack = false;

    public ExileRegistryContainer<C> setIsDatapack() {
        this.dataPacksAreRegistered = false;
        this.isDatapack = true;

        return this;
    }

    private HashMap<String, C> serializables = new HashMap<>();

    public List<C> getSerializable() {
        return new ArrayList<>(serializables.values());
    }

    List<C> fromDatapacks = null;
    FriendlyByteBuf cachedBuf = null;


    public void sendUpdatePacket(ServerPlayer player) {
        if (type.ser == null) {
            return;
        }
        if (LibraryOfExile.DISABLE_DATABASE_DATAPACK_FEATURE) {
            return;
        }

        Preconditions.checkNotNull(cachedBuf, type.id + " error, cachedbuf is null!!!");

        Packets.sendToClient(player, new EfficientRegistryPacket(this.type, Database.getRegistry(type).getFromDatapacks()));

    }

    public void onAllDatapacksLoaded() {

        fromDatapacks = null;
        getFromDatapacks();

        if (fromDatapacks != null && !fromDatapacks.isEmpty()) {
            cachedBuf = new FriendlyByteBuf(Unpooled.buffer());
            // save the packetbytebuf, this should save at least 0.1 sec for each time anyone logs in.
            // SUPER important for big mmorpg servers!

            new EfficientRegistryPacket(type, Database.getRegistry(type).getFromDatapacks()).saveToData(cachedBuf);

            Preconditions.checkNotNull(cachedBuf);
        }
    }

    public List<C> getFromDatapacks() {
        if (fromDatapacks == null) {// cache this cus it's called every login
            fromDatapacks = getList().stream()
                    .filter(x -> x.isFromDatapack())
                    .collect(Collectors.toList());
        }
        return fromDatapacks;
    }

    public ExileRegistryType getType() {
        return type;
    }

    public static void logRegistryError(String text) {
        try {
            throw new Exception("[Mine and Slash Registry Error]: " + text);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private ExileRegistryType type;
    private String emptyDefault;

    public C getDefault() {
        return this.map.getOrDefault(emptyDefault, null);
    }

    private HashMap<String, C> map = new HashMap<>();
    private boolean errorIfEmpty = true;
    private boolean logAdditionsToRegistry = false;
    private boolean logMissingEntryOnAccess = true;

    public ExileRegistryContainer logAdditions() {
        this.logAdditionsToRegistry = true;
        return this;
    }

    public void unRegister(ExileRegistry entry) {
        if (map.containsKey(entry.GUID())) {
            map.remove(entry.GUID());
        }
    }

    public ExileRegistryContainer dontErrorMissingEntriesOnAccess() {
        this.logMissingEntryOnAccess = false;
        return this;
    }

    public ExileRegistryContainer dontErrorIfEmpty() {
        this.errorIfEmpty = false;
        return this;
    }

    public int getSize() {
        return map.size();
    }

    public boolean isRegistrationDone() {
        return getSize() > 0;
    }

    public ExileRegistryContainer(ExileRegistryType type, String emptyDefault) {
        this.type = type;
        this.emptyDefault = emptyDefault;
    }


    private void tryLogEmptyRegistry() {
        if (errorIfEmpty) {
            if (map.isEmpty()) {
                if (this.dataPacksAreRegistered) { // dont error for client side stuff if datapacks have yet to arrive from packets
                    if (emptyRegistries.contains(this.type.id)) {

                        emptyRegistries.add(this.type.id);
                        logRegistryError(
                                "Exile Registry of type: " + this.type.toString() + " is empty, this is really bad!");
                    }
                }
            }
        }
    }

    public HashMap<String, C> getAll() {

        tryLogEmptyRegistry();

        return map;
    }

    public List<C> getList() {
        if (!map.isEmpty()) {
            return new ArrayList<C>(map.values());
        }

        this.tryLogEmptyRegistry();

        return Arrays.asList();
    }

    public List<C> getAllIncludingSeriazable() {
        List<C> list = new ArrayList<C>(map.values());
        list.addAll(serializables.values());
        return list;
    }


    public C getFromSerializables(DataGenKey<C> key) {
        return this.serializables.get(key.GUID());
    }

    public C getFromSerializables(String key) {
        return this.serializables.get(key);
    }

    boolean accessedEarly = false;

    public C get(String guid) {

        if (map.isEmpty() && serializables.isEmpty()) {
            if (!accessedEarly) {
                throw new RuntimeException("\n Accessed slash registry earlier than datapacks are loaded, returning empty: " + guid + "\n");
            }
            accessedEarly = true;
            return this.getDefault();
        }

        tryLogEmptyRegistry();

        if (guid == null || guid.isEmpty()) {
            return getDefault();
        }
        if (map.containsKey(guid)) {
            return map.get(guid);
        } else if (serializables.containsKey(guid)) {
            return serializables.get(guid);
        } else {
            if (logMissingEntryOnAccess) {
                if (accessorErrosAletedFor.contains(guid) == false) {
                    logRegistryError(
                            "GUID Error: " + guid + " of type: " + type.id + " doesn't exist. This is either " + "a removed/renamed old registry, or robertx22 forgot to include it in an " + "update" + ".");
                    accessorErrosAletedFor.add(guid);
                }
            }
            return getDefault();
        }
    }

    public FilterListWrap<C> getWrapped() {
        return new FilterListWrap<C>(this.map.values());
    }

    public FilterListWrap<C> getFilterWrapped(Predicate<C> pred) {
        return new FilterListWrap<C>(getFiltered(pred));
    }

    // just do gearsThatCanDoThis.and() .or() etc. if need multiple
    public List<C> getFiltered(Predicate<C> predicate) {
        return this.getList()
                .stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public C random() {
        return RandomUtils.weightedRandom(this.getList());
    }

    public C random(Double randomDouble) {
        return RandomUtils.weightedRandom(this.getList(), randomDouble);
    }

    public boolean isRegistered(C c) {
        return isRegistered(c.GUID());
    }

    public boolean isRegistered(String guid) {
        return map.containsKey(guid);
    }

    public boolean isExistingSeriazable(String guid) {
        return map.containsKey(guid);
    }


    public void register(C c, ExileRegistrationInfo info) {

        Preconditions.checkNotNull(info);

        if (isRegistered(c)) {
            if (registersErrorsAlertedFor.contains(c.GUID()) == false) {
                logRegistryError("Key: " + c.GUID() + " has already been registered to: " + c.getExileRegistryType()
                        .toString() + " registry.");
                registersErrorsAlertedFor.add(c.GUID());
            }

        } else {
            tryLogAddition(c);
            map.put(c.GUID(), c);
            if (!registrationInfo.containsKey(c.GUID())) {
                registrationInfo.put(c.GUID(), new RegistrationInfoData());
            }
            registrationInfo.get(c.GUID()).onRegister(info);

            ExileEvents.ON_REGISTER_TO_DATABASE.callEvents(new ExileEvents.OnRegisterToDatabase(c, type));
        }

    }

    private void tryLogAddition(C c) {

        //   if (logAdditionsToRegistry /*&& ModConfig.get().Server.LOG_REGISTRY_ENTRIES*/// todo
           /*
           System.out.println(
                "[Age of Exile Registry Addition]: " + c.GUID() + " to " + type.toString() + " registry");
        }
        */

    }

    public void addSerializable(C entry, ExileRegistrationInfo info) {
        if (serializables.containsKey(entry.GUID())) {
            ExileLog.get().warn("Entry of type: " + entry.getExileRegistryType().id + " already exists as seriazable: " + entry.GUID());
        }
        this.serializables.put(entry.GUID(), entry);
        this.unRegister(entry);
        this.register(entry, info);

    }

    public boolean isEmpty() {
        return map.isEmpty();
    }
}
