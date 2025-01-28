package com.robertx22.library_of_exile.config.map_dimension;

import net.minecraft.world.level.GameRules;

// override some rules specifically on mapdimensions
public class MapGameRules extends GameRules {

    public GameRules rules;

    public MapGameRules(GameRules rules) {
        this.rules = rules;
    }

    @Override
    public int getInt(GameRules.Key<GameRules.IntegerValue> key) {
        if (key == GameRules.RULE_RANDOMTICKING) {
            return 0;
        }
        return rules.getRule(key).get();
    }

    @Override
    public boolean getBoolean(GameRules.Key<GameRules.BooleanValue> key) {
        if (key == GameRules.RULE_DOFIRETICK) {
            return false;
        }
        // todo i wanted to put this too BUT it's buggy! Doesn't work return true;
/*
        if (key == GameRules.RULE_KEEPINVENTORY) {
        }
 */
        if (key == GameRules.RULE_DO_VINES_SPREAD) {
            return false;
        }
        if (key == GameRules.RULE_MOBGRIEFING) {
            return false;
        }
        return rules.getRule(key).get();
    }

}
