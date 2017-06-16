package com.guok.hap.impl.characteristics.thermostat;

import java.util.concurrent.CompletableFuture;

import com.guok.hap.accessories.thermostat.BasicThermostat;
import com.guok.hap.characteristics.EnumCharacteristic;

public class TemperatureUnitsCharacteristic extends EnumCharacteristic {

	private final BasicThermostat thermostat;
	
	public TemperatureUnitsCharacteristic(BasicThermostat thermostat) {
		super("00000036-0000-1000-8000-0026BB765291", false, true, "The temperature unit", 1);
		this.thermostat = thermostat;
	}

	@Override
	protected void setValue(Integer value) throws Exception {
		//Not writable
	}

	@Override
	protected CompletableFuture<Integer> getValue() {
		return CompletableFuture.completedFuture(thermostat.getTemperatureUnit().getCode());
	}

}
