package de.htw.lambtenance.properties;

import java.text.DecimalFormat;
import java.util.Random;

public abstract class Property {

    private static DecimalFormat DF = new DecimalFormat("###.#####");

    private String _unit;
    private String _description;

    private double _baseValue;
    private final double _idealValue;


    public Property(double value, String unit, String description) {
        _baseValue = value;
        _idealValue = value;
        _unit = unit;
        _description = description;
    }

    public void generateAnomaly() {
        _baseValue *= 1.5;
    }

    public void generateError() {
        _baseValue *= 0.0;
    }

    private double generateRealisticValue() {
        Random ran = new Random();
        double max = 1.2;
        double min = 0.8;
        double d = min + (max - min) * ran.nextDouble();
        return d * _baseValue;
    }

    public void resetToAcceptableValue() {
        _baseValue = _idealValue;
    }


    public String getUnit() { return _unit; }

    public String getDescription() { return _description; }

    public double getRealisticValue() { return generateRealisticValue(); }

    @Override
    public String toString() {
        return _description + ": " +
                DF.format(generateRealisticValue()) + " " +
                _unit;
    }

}
