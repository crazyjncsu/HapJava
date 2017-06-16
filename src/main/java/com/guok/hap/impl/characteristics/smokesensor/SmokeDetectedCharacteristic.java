package com.guok.hap.impl.characteristics.smokesensor;

import com.guok.hap.HomekitCharacteristicChangeCallback;
import com.guok.hap.accessories.SmokeSensor;
import com.guok.hap.accessories.properties.SmokeDetectedState;
import com.guok.hap.characteristics.EnumCharacteristic;
import com.guok.hap.characteristics.EventableCharacteristic;

import java.util.concurrent.CompletableFuture;

public class SmokeDetectedCharacteristic extends EnumCharacteristic implements EventableCharacteristic {

    private final SmokeSensor smokeSensor;

    public SmokeDetectedCharacteristic(SmokeSensor smokeSensor) {
        super("00000076-0000-1000-8000-0026BB765291", false, true, "Smoke Detected", 1);
        this.smokeSensor = smokeSensor;
    }

    @Override
    protected CompletableFuture<Integer> getValue() {
        return smokeSensor.getSmokeDetectedState().thenApply(SmokeDetectedState::getCode);
    }

    @Override
    protected void setValue(Integer value) throws Exception {
        //Read Only
    }

    @Override
    public void subscribe(HomekitCharacteristicChangeCallback callback) {
        smokeSensor.subscribeSmokeDetectedState(callback);
    }

    @Override
    public void unsubscribe() {
        smokeSensor.unsubscribeSmokeDetectedState();
    }
}
