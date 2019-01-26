package de.htw.lambtenance.machines;

import de.htw.lambtenance.properties.EnergyConsumption;
import de.htw.lambtenance.properties.Speed;

public class ConveyorBelt extends Machine {

    private static int NEXT_ID = 1;
    private static String DESCRIPTION = "Conveyor Belt";

    private EnergyConsumption energyConsumption;
    private Speed speed;

    public ConveyorBelt() {
        super(DESCRIPTION);

        energyConsumption = new EnergyConsumption();
        speed = new Speed();

        properties.add(energyConsumption);
        properties.add(speed);

        id = NEXT_ID++;
    }

}
