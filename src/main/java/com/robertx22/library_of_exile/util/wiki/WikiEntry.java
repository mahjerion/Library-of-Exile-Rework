package com.robertx22.library_of_exile.util.wiki;

public class WikiEntry {

    public boolean use;
    public String text;

    private WikiEntry(boolean use, String text) {
        this.use = use;
        this.text = text;
    }

    public static WikiEntry none() {
        return new WikiEntry(false, "");
    }

    public static WikiEntry of(String text) {
        return new WikiEntry(true, text);
    }
}
