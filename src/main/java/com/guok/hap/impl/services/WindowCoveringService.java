package com.guok.hap.impl.services;

import com.guok.hap.accessories.HorizontalTiltingWindowCovering;
import com.guok.hap.accessories.VerticalTiltingWindowCovering;
import com.guok.hap.accessories.WindowCovering;
import com.guok.hap.impl.characteristics.common.ObstructionDetectedCharacteristic;
import com.guok.hap.impl.characteristics.windowcovering.*;

public class WindowCoveringService extends AbstractServiceImpl {

	public WindowCoveringService(WindowCovering windowCovering) {
		this(windowCovering, windowCovering.getLabel());
	}

	public WindowCoveringService(WindowCovering windowCovering, String serviceName) {
		super("0000008C-0000-1000-8000-0026BB765291", windowCovering, serviceName);
		addCharacteristic(new CurrentPositionCharacteristic(windowCovering));
		addCharacteristic(new HoldPositionCharacteristic(windowCovering));
		addCharacteristic(new PositionStateCharacteristic(windowCovering));
		addCharacteristic(new TargetPositionCharacteristic(windowCovering));
		addCharacteristic(new ObstructionDetectedCharacteristic(() -> windowCovering.getObstructionDetected(),
				c -> windowCovering.subscribeObstructionDetected(c),
				() -> windowCovering.unsubscribeObstructionDetected()));

		if (windowCovering instanceof HorizontalTiltingWindowCovering) {
			addCharacteristic(new CurrentHorizontalTiltAngleCharacteristic(
					(HorizontalTiltingWindowCovering) windowCovering));
			addCharacteristic(new TargetHorizontalTiltAngleCharacteristic(
					(HorizontalTiltingWindowCovering) windowCovering));
		}
		if (windowCovering instanceof VerticalTiltingWindowCovering) {
			addCharacteristic(new CurrentVerticalTiltAngleCharacteristic(
					(VerticalTiltingWindowCovering) windowCovering));
			addCharacteristic(new TargetVerticalTiltAngleCharacteristic(
					(VerticalTiltingWindowCovering) windowCovering));
		}
	}
}
