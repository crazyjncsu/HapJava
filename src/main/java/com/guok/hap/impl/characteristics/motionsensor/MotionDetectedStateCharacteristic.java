package com.guok.hap.impl.characteristics.motionsensor;

import com.google.common.util.concurrent.ListenableFuture;

import com.guok.hap.HomekitCharacteristicChangeCallback;
import com.guok.hap.accessories.MotionSensor;
import com.guok.hap.characteristics.BooleanCharacteristic;
import com.guok.hap.characteristics.EventableCharacteristic;

public class MotionDetectedStateCharacteristic extends BooleanCharacteristic implements EventableCharacteristic {

    private final MotionSensor motionSensor;

    public MotionDetectedStateCharacteristic(MotionSensor motionSensor) {
        super("00000022-0000-1000-8000-0026BB765291", false, true, "Motion Detected");
        this.motionSensor = motionSensor;
    }

    @Override
    protected ListenableFuture<Boolean> getValue() {
        return motionSensor.getMotionDetected();
    }

    @Override
    protected void setValue(Boolean value) throws Exception {
        //Read Only
    }

    @Override
    public void subscribe(HomekitCharacteristicChangeCallback callback) {
        motionSensor.subscribeMotionDetected(callback);
    }

    @Override
    public void unsubscribe() {
        motionSensor.unsubscribeMotionDetected();
    }
}
