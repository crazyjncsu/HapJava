package com.guok.hap.impl.characteristics.securitysystem;

import com.guok.hap.HomekitCharacteristicChangeCallback;
import com.guok.hap.accessories.SecuritySystem;
import com.guok.hap.accessories.properties.CurrentSecuritySystemState;
import com.guok.hap.characteristics.EnumCharacteristic;
import com.guok.hap.characteristics.EventableCharacteristic;

import java.util.concurrent.CompletableFuture;

public class CurrentSecuritySystemStateCharacteristic extends EnumCharacteristic implements EventableCharacteristic {

    private final SecuritySystem securitySystem;

    public CurrentSecuritySystemStateCharacteristic(SecuritySystem securitySystem) {
        super("00000066-0000-1000-8000-0026BB765291", false, true, "Current security system state", 4);
        this.securitySystem = securitySystem;
    }

    @Override
    protected CompletableFuture<Integer> getValue() {
        return securitySystem.getCurrentSecuritySystemState().thenApply(CurrentSecuritySystemState::getCode);
    }

    @Override
    protected void setValue(Integer value) throws Exception {
        //Not writable
    }

    @Override
    public void subscribe(HomekitCharacteristicChangeCallback callback) {
        securitySystem.subscribeCurrentSecuritySystemState(callback);
    }

    @Override
    public void unsubscribe() {
        securitySystem.unsubscribeCurrentSecuritySystemState();
    }
}
