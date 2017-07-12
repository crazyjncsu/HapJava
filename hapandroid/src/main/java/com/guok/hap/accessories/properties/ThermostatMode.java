package com.guok.hap.accessories.properties;

import com.guok.hap.accessories.thermostat.BasicThermostat;

import java.util.HashMap;
import java.util.Map;

/**
 * The mode used by a {@link BasicThermostat}
 *
 * @author Andy Lintner
 */
public enum ThermostatMode {

	OFF(0),
	HEAT(1),
	COOL(2),
	AUTO(3)
	;
	
	private final static Map<Integer, ThermostatMode> reverse;
	static {
//		reverse = Arrays.stream(ThermostatMode.values()).collect(Collectors.toMap(t -> t.getCode(), t -> t));
		reverse = new HashMap<>();
		for (ThermostatMode state : ThermostatMode.values()) {
			reverse.put(state.getCode(), state);
		}
	}
	
	public static ThermostatMode fromCode(Integer code) {
		return reverse.get(code);
	}
	
	private final int code;
	
	private ThermostatMode(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
}
