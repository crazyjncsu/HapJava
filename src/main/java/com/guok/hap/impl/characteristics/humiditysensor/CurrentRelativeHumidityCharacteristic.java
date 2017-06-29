package com.guok.hap.impl.characteristics.humiditysensor;

import com.google.common.util.concurrent.ListenableFuture;

import com.guok.hap.HomekitCharacteristicChangeCallback;
import com.guok.hap.accessories.HumiditySensor;
import com.guok.hap.characteristics.CharacteristicUnits;
import com.guok.hap.characteristics.EventableCharacteristic;
import com.guok.hap.characteristics.FloatCharacteristic;

public class CurrentRelativeHumidityCharacteristic extends FloatCharacteristic implements EventableCharacteristic {

	private final HumiditySensor sensor;
	
	public CurrentRelativeHumidityCharacteristic(HumiditySensor sensor) {
		super("00000010-0000-1000-8000-0026BB765291", false, true, "Current relative humidity", 0, 100,
				0.1, CharacteristicUnits.percentage);
		this.sensor = sensor;
	}
	
	@Override
	public void subscribe(HomekitCharacteristicChangeCallback callback) {
		sensor.subscribeCurrentRelativeHumidity(callback);
	}

	@Override
	public void unsubscribe() {
		sensor.unsubscribeCurrentRelativeHumidity();
	}

	@Override
	protected void setValue(Double value) throws Exception {
		//Read Only
	}

	@Override
	protected ListenableFuture<Double> getDoubleValue() {
		return sensor.getCurrentRelativeHumidity();
	}
}
