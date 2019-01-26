package de.htw.lambtenance.machines;

import de.htw.lambtenance.properties.EnergyConsumption;
import de.htw.lambtenance.properties.Temperature;

public class Oven extends Machine {

    private static int NEXT_ID = 1;
    private static String DESCRIPTION = "Oven";

    private EnergyConsumption energyConsumption;
    private Temperature temperature;

    public Oven() {
        super(DESCRIPTION);

        energyConsumption = new EnergyConsumption();
        temperature = new Temperature();

        properties.add(energyConsumption);
        properties.add(temperature);

        id = NEXT_ID++;
    }

}
