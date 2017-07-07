package com.guok.hap.accessories.properties;

import com.guok.hap.accessories.WindowCovering;

import java.util.HashMap;
import java.util.Map;

/**
 * The position state used by a {@link WindowCovering}
 *
 * @author Andy Lintner
 */
public enum WindowCoveringPositionState {
	DECREASING(0),
	INCREASING(1),
	STOPPED(2)
	;
	
	private final static Map<Integer, WindowCoveringPositionState> reverse;
	static {
//		reverse = Arrays.stream(WindowCoveringPositionState.values()).collect(Collectors.toMap(t -> t.getCode(), t -> t));
		reverse = new HashMap<>();
		for (WindowCoveringPositionState state : WindowCoveringPositionState.values()) {
			reverse.put(state.getCode(), state);
		}
	}
	
	public static WindowCoveringPositionState fromCode(Integer code) {
		return reverse.get(code);
	}
	
	private final int code;
	
	private WindowCoveringPositionState(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
}
