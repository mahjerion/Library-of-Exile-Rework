package com.robertx22.library_of_exile.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.StackLocatorUtil;

// always use this to log stuff
public class ExileLog {

    private final Logger LOGGER;

    public ExileLog() {
        var name = LogManager.getLogger(StackLocatorUtil.getCallerClass(2));
        this.LOGGER = LogManager.getLogger("Exile Log: " + name.getName());
    }

    public void warn(String str, Object... obj) {
        LOGGER.warn(str, obj);
    }

    public void log(String str, Object... obj) {
        LOGGER.info(str, obj);
    }

    public void onlyInConsole(String str) {
        System.out.println(str);
    }

}
