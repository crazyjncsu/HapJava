package com.guok.hap.impl.services;

import com.guok.hap.accessories.MotionSensor;
import com.guok.hap.impl.characteristics.motionsensor.MotionDetectedStateCharacteristic;

public class MotionSensorService extends AbstractServiceImpl {

    public MotionSensorService(MotionSensor motionSensor) {
        this(motionSensor, motionSensor.getLabel());
    }

    public MotionSensorService(MotionSensor motionSensor, String serviceName) {
        super("00000085-0000-1000-8000-0026BB765291", motionSensor, serviceName);
        addCharacteristic(new MotionDetectedStateCharacteristic(motionSensor));
    }
}
