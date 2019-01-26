package de.htw.lambtenance.machines;

import de.htw.lambtenance.properties.RotationSpeed;
import de.htw.lambtenance.properties.Temperature;
import de.htw.lambtenance.properties.ThicknessOfProduct;

public class RoomAirDistribution extends Machine {

    private static int NEXT_ID = 1;
    private static String DESCRIPTION = "Room Air Distribution";

    private RotationSpeed rotationSpeed;
    private Temperature temperature;
    private ThicknessOfProduct thicknessOfProduct;

    public RoomAirDistribution() {
        super(DESCRIPTION);
        rotationSpeed = new RotationSpeed();
        temperature = new Temperature();
        thicknessOfProduct = new ThicknessOfProduct();

        properties.add(rotationSpeed);
        properties.add(temperature);
        properties.add(thicknessOfProduct);

        id = NEXT_ID++;
    }

}
