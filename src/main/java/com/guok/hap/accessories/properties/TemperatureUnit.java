package com.guok.hap.accessories.properties;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import com.guok.hap.accessories.TemperatureSensor;

/**
 * The temperature unit used by a {@link TemperatureSensor}.
 *
 * @author Andy Lintner
 */
public enum TemperatureUnit {

	CELSIUS(0),
	FAHRENHEIT(1)
	;
	
	private final static Map<Integer, TemperatureUnit> reverse;
	static {
		reverse = Arrays.stream(TemperatureUnit.values()).collect(Collectors.toMap(t -> t.getCode(), t -> t));
	}
	
	static TemperatureUnit fromCode(Integer code) {
		return reverse.get(code);
	}
	
	private final int code;
	
	private TemperatureUnit(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
}
