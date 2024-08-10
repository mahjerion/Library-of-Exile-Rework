package com.robertx22.library_of_exile.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// always use this to log stuff
public class ExileLog {

    private final Logger LOGGER;

    public ExileLog() {
        this.LOGGER = LogManager.getLogger();
    }

    public ExileLog(String ref) {
        this.LOGGER = LogManager.getLogger(ref);
    }

    public static ExileLog getWithRef(String ref){
        return new ExileLog(ref);
    }

    public void warn(String str, Object... obj){
        LOGGER.warn(str, obj);
    }
    public void log(String str, Object... obj) {
        LOGGER.info(str, obj);
    }

    public void onlyInConsole(String str) {
        System.out.println(str);
    }

}
