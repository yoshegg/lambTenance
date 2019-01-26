package de.htw.lambtenance.machines;

import de.htw.lambtenance.properties.EnergyConsumption;
import de.htw.lambtenance.properties.Precision;
import de.htw.lambtenance.properties.Pressure;
import de.htw.lambtenance.properties.Temperature;

public class PunchingMachine extends Machine {

    private static int NEXT_ID = 1;
    private static String DESCRIPTION = "Punching Machine";

    private EnergyConsumption energyConsumption;
    private Precision precision;
    private Temperature temperature;
    private Pressure pressure;

    public PunchingMachine() {
        super(DESCRIPTION);

        energyConsumption = new EnergyConsumption();
        precision = new Precision();
        temperature = new Temperature();
        pressure = new Pressure();

        properties.add(energyConsumption);
        properties.add(precision);
        properties.add(temperature);
        properties.add(pressure);


        id = NEXT_ID++;
    }

}
