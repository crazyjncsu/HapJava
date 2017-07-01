package com.guok.hap.impl.characteristics.windowcovering;

import com.google.common.util.concurrent.ListenableFuture;

import com.guok.hap.HomekitCharacteristicChangeCallback;
import com.guok.hap.accessories.VerticalTiltingWindowCovering;
import com.guok.hap.characteristics.CharacteristicUnits;
import com.guok.hap.characteristics.EventableCharacteristic;
import com.guok.hap.characteristics.IntegerCharacteristic;
import com.guok.hap.impl.responses.HapStatusCodes;

public class CurrentVerticalTiltAngleCharacteristic extends IntegerCharacteristic implements EventableCharacteristic {

	private final VerticalTiltingWindowCovering windowCovering;
	
	public CurrentVerticalTiltAngleCharacteristic(VerticalTiltingWindowCovering windowCovering) {
		super("0000006E-0000-1000-8000-0026BB765291", false, true, "The current vertical tilt angle", -90, 90, CharacteristicUnits.arcdegrees);
		this.windowCovering = windowCovering;
	}

	@Override
	protected int setValue(Integer value) throws Exception {
		//Read Only
		return HapStatusCodes.READ_OLNY;
	}

	@Override
	protected ListenableFuture<Integer> getValue() {
		return windowCovering.getCurrentVerticalTiltAngle();
	}

	@Override
	public void subscribe(HomekitCharacteristicChangeCallback callback) {
		windowCovering.subscribeCurrentVerticalTiltAngle(callback);
	}

	@Override
	public void unsubscribe() {
		windowCovering.unsubscribeCurrentVerticalTiltAngle();
	}

}
