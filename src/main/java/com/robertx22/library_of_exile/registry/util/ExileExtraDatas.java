package com.robertx22.library_of_exile.registry.util;

public class ExileExtraDatas {

    public static ExileExtraData<RegisterInfo> MODID_OF_SERIAZABLE = new ExileExtraData<>(() -> new RegisterInfo());


    public static class RegisterInfo {
        private String modid = "";

        public String getModid() {
            return modid;
        }

        public void set(String id) {

            if (id.isEmpty()) {
                throw new RuntimeException("empty id");
            }

            this.modid = id;
        }
    }

    public static void init() {


    }

}
