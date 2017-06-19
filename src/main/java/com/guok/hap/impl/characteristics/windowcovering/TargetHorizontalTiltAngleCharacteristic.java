package com.guok.hap.impl.characteristics.windowcovering;

import com.google.common.util.concurrent.ListenableFuture;

import com.guok.hap.HomekitCharacteristicChangeCallback;
import com.guok.hap.accessories.HorizontalTiltingWindowCovering;
import com.guok.hap.characteristics.EventableCharacteristic;
import com.guok.hap.characteristics.IntegerCharacteristic;

public class TargetHorizontalTiltAngleCharacteristic extends IntegerCharacteristic implements EventableCharacteristic {

	private final HorizontalTiltingWindowCovering windowCovering;
	
	public TargetHorizontalTiltAngleCharacteristic(HorizontalTiltingWindowCovering windowCovering) {
		super("0000007B-0000-1000-8000-0026BB765291", true, true, "The target horizontal tilt angle", -90, 90, "Arc Degree");
		this.windowCovering = windowCovering;
	}

	@Override
	protected void setValue(Integer value) throws Exception {
		windowCovering.setTargetHorizontalTiltAngle(value);
	}

	@Override
	protected ListenableFuture<Integer> getValue() {
		return windowCovering.getTargetHorizontalTiltAngle();
	}

	@Override
	public void subscribe(HomekitCharacteristicChangeCallback callback) {
		windowCovering.subscribeTargetHorizontalTiltAngle(callback);
	}

	@Override
	public void unsubscribe() {
		windowCovering.unsubscribeTargetHorizontalTiltAngle();
	}

}
