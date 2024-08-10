package com.robertx22.library_of_exile.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.StackLocatorUtil;

// always use this to log stuff
public class ExileLog {

    private Logger LOGGER;

    private ExileLog(Logger LOGGER) {
        this.LOGGER = LOGGER;
    }

    public static ExileLog get() {
        var name = LogManager.getLogger(StackLocatorUtil.getCallerClass(2));
        ExileLog b = new ExileLog(LogManager.getLogger("Exile Log: " + name.getName()));
        return b;
    }

    public void warn(String str, Object... obj) {
        LOGGER.warn(str, obj);
    }

    public void log(String str, Object... obj) {
        LOGGER.info(str, obj);
    }

    public void debug(String str, Object... obj) {
        LOGGER.debug(str, obj);
    }

    public void onlyInConsole(String str) {
        System.out.println(str);
    }

}
