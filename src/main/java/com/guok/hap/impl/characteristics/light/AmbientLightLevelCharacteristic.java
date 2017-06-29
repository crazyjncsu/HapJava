package com.guok.hap.impl.characteristics.light;

import com.google.common.util.concurrent.ListenableFuture;

import com.guok.hap.HomekitCharacteristicChangeCallback;
import com.guok.hap.accessories.LightSensor;
import com.guok.hap.characteristics.CharacteristicUnits;
import com.guok.hap.characteristics.EventableCharacteristic;
import com.guok.hap.characteristics.FloatCharacteristic;

public class AmbientLightLevelCharacteristic extends FloatCharacteristic implements EventableCharacteristic {

    private final LightSensor lightSensor;

    public AmbientLightLevelCharacteristic(LightSensor lightSensor) {
        super("0000006B-0000-1000-8000-0026BB765291", false, true, "Current ambient light level", 0.0001, 100000,
                0.0001, CharacteristicUnits.lux);
        this.lightSensor = lightSensor;
    }

    @Override
    protected void setValue(Double value) throws Exception {
        //Read Only
    }

    @Override
    public void subscribe(HomekitCharacteristicChangeCallback callback) {
        lightSensor.subscribeCurrentAmbientLightLevel(callback);
    }

    @Override
    public void unsubscribe() {
        lightSensor.unsubscribeCurrentAmbientLightLevel();
    }

    @Override
    protected ListenableFuture<Double> getDoubleValue() {
        return lightSensor.getCurrentAmbientLightLevel();
    }
}
