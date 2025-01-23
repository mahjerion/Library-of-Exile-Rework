package com.robertx22.library_of_exile.database.affix.base;

import java.text.DecimalFormat;

public class AffixTranslation {
    public String modid;
    public String locname;

    public AffixTranslation(String modid, String locname) {
        this.modid = modid;
        this.locname = locname;
    }

    public static AffixTranslation ofAttribute(String modid) {
        if (true) {
            // we dont  need this if vanilla has code for it already?
            return new AffixTranslation(modid, "");
        }
        return new AffixTranslation(modid, "%1$s %2$s");
    }

    public static AffixTranslation ofPotion(String modid) {
        // Gain [Speed]1 [II]2 for [3s] every [10s]
        return new AffixTranslation(modid, "Gain %1$s %2$s for %3$ss every %4$ss");
    }

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    public static String formatNumber(float num) {
        if (num < 10) {
            return DECIMAL_FORMAT.format(num);
        } else {
            return ((int) num) + "";
        }
    }
}
