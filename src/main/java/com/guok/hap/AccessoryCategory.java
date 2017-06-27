package com.guok.hap;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by guokai on 26/06/2017.
 */
public enum AccessoryCategory {
    OTHER(1),
    BRIDGE(2),
    FAN(3),
    GARAGE(4),
    LIGHTBULB(5),
    DOOR_LOCK(6),
    OUTLEY(7),
    SWITCH(8),
    THERMOSTAT(9),
    SENSOR(10),
    SECURITY_SYSTEM(11),
    DOOR(12),
    WINDOW(13),
    WINDOW_COVERING(14),
    PROGRAMMABLE_SWITCH(15),
    RANGE_EXTENDER(16),
    IP_CAMERA(17),
    VIDEO_DOOR_BELL(18),
    AIR_PURIFIER(19),
    RESERVED(20);


    private final static Map<Integer, AccessoryCategory> reverse;

    static {
        reverse = new HashMap<>();
        for (AccessoryCategory state : AccessoryCategory.values()) {
            reverse.put(state.getCode(), state);
        }
    }

    public static AccessoryCategory fromCode(Integer code) {
        return reverse.get(code);
    }

    private final int code;

    AccessoryCategory(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
