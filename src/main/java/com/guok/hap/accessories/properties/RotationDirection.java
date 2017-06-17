package com.guok.hap.accessories.properties;

import java.util.HashMap;
import java.util.Map;

public enum RotationDirection {
	CLOCKWISE(0),
	COUNTER_CLOCKWISE(1)
	;
	
	private final static Map<Integer, RotationDirection> reverse;
	static {
//		reverse = Arrays.stream(RotationDirection.values()).collect(Collectors.toMap(t -> t.getCode(), t -> t));

		reverse = new HashMap<>();
		for (RotationDirection state : RotationDirection.values()) {
			reverse.put(state.getCode(), state);
		}
	}
	
	public static RotationDirection fromCode(Integer code) {
		return reverse.get(code);
	}
	
	private final int code;
	
	private RotationDirection(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
}
