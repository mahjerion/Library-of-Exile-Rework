package com.robertx22.library_of_exile.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// always use this to log stuff
public class ExileLog {

    private Logger LOGGER = LogManager.getLogger();

    public void log(String str, Object... obj) {
        LOGGER.info(str, obj);
    }

    public void onlyInConsole(String str) {
        System.out.println(str);
    }

}
