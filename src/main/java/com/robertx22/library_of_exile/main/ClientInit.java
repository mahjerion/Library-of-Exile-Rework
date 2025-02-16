package com.robertx22.library_of_exile.main;

import com.robertx22.library_of_exile.unidentified.IdentifiableItemsClient;

public class ClientInit {

    public static void onInitializeClient() {
        IdentifiableItemsClient.init();
    }
}
