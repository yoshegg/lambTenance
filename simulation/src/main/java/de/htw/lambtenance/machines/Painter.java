package de.htw.lambtenance.machines;

import de.htw.lambtenance.properties.AmountOfPaint;
import de.htw.lambtenance.properties.CoverageRatio;
import de.htw.lambtenance.properties.EnergyConsumption;
import de.htw.lambtenance.properties.Temperature;

public class Painter extends Machine {

    private static int NEXT_ID = 1;
    private static String DESCRIPTION = "Painter";

    private EnergyConsumption energyConsumption;
    private AmountOfPaint amountOfPaint;
    private CoverageRatio coverageRatio;
    private Temperature temperature;

    public Painter() {
        super(DESCRIPTION);

        energyConsumption = new EnergyConsumption();
        amountOfPaint = new AmountOfPaint();
        coverageRatio = new CoverageRatio();
        temperature = new Temperature();

        properties.add(energyConsumption);
        properties.add(amountOfPaint);
        properties.add(coverageRatio);
        properties.add(coverageRatio);

        id = NEXT_ID++;
    }

}
