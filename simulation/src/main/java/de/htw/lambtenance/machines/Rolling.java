package de.htw.lambtenance.machines;

import de.htw.lambtenance.properties.*;

/**
 * German: Walze
 */
public class Rolling extends Machine {

    private static int NEXT_ID = 1;
    private static String DESCRIPTION = "Rolling Machine";

    private Pressure pressure;
    private Temperature temperature;
    private Abrasion abrasion;
    private EnergyConsumption energyConsumption;
    private ThicknessOfProduct thicknessOfProduct;

    public Rolling() {
        super(DESCRIPTION);
        pressure = new Pressure();
        temperature = new Temperature();
        abrasion = new Abrasion();
        energyConsumption = new EnergyConsumption();
        thicknessOfProduct = new ThicknessOfProduct();

        properties.add(pressure);
        properties.add(temperature);
        properties.add(abrasion);
        properties.add(energyConsumption);
        properties.add(thicknessOfProduct);

        id = NEXT_ID++;
    }

}
