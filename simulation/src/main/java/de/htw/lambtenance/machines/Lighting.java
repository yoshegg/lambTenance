package de.htw.lambtenance.machines;

import de.htw.lambtenance.properties.Brightness;
import de.htw.lambtenance.properties.EnergyConsumption;

public class Lighting extends Machine {

    private static int NEXT_ID = 1;
    private static String DESCRIPTION = "Lightning";

    private EnergyConsumption energyConsumption;
    private Brightness brightness;

    public Lighting() {
        super(DESCRIPTION);

        energyConsumption = new EnergyConsumption();
        brightness = new Brightness();

        properties.add(energyConsumption);
        properties.add(brightness);

        id = NEXT_ID++;
    }

}
