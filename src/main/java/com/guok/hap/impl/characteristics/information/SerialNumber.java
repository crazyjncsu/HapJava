package com.guok.hap.impl.characteristics.information;

import com.guok.hap.HomekitAccessory;
import com.guok.hap.characteristics.StaticStringCharacteristic;

public class SerialNumber extends StaticStringCharacteristic {

    public static final String UUID = "00000030-0000-1000-8000-0026BB765291";

    public SerialNumber(HomekitAccessory accessory) throws Exception {
        super(UUID, "The serial number of the accessory", accessory.getSerialNumber());
    }

}
