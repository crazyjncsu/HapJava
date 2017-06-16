package com.guok.hap.impl.characteristics.fan;

import java.util.concurrent.CompletableFuture;

import com.guok.hap.HomekitCharacteristicChangeCallback;
import com.guok.hap.accessories.Fan;
import com.guok.hap.accessories.properties.RotationDirection;
import com.guok.hap.characteristics.EnumCharacteristic;
import com.guok.hap.characteristics.EventableCharacteristic;

public class RotationDirectionCharacteristic extends EnumCharacteristic implements EventableCharacteristic {

	private final Fan fan;
	
	public RotationDirectionCharacteristic(Fan fan) {
		super("00000028-0000-1000-8000-0026BB765291", true, true, "Rotation Direction", 1);
		this.fan = fan;
	}

	@Override
	protected void setValue(Integer value) throws Exception {
		fan.setRotationDirection(RotationDirection.fromCode(value));
	}

	@Override
	protected CompletableFuture<Integer> getValue() {
		return fan.getRotationDirection().thenApply(s -> s.getCode());
	}

	@Override
	public void subscribe(HomekitCharacteristicChangeCallback callback) {
		fan.subscribeRotationDirection(callback);
	}

	@Override
	public void unsubscribe() {
		fan.unsubscribeRotationDirection();
	}

}
