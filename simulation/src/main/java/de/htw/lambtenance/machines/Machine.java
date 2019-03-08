package de.htw.lambtenance.machines;

import de.htw.lambtenance.properties.Property;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents interface for all kinds of machines.
 */
public abstract class Machine {

    public int id;

    private String _description;

    public Set<Property> properties = new HashSet<>();

    public Machine(String description) {
        _description = description;
    }

    private String generateDummyData() {
        StringBuilder output = new StringBuilder();
        output.append(_description + " (" + id + ")\n");
        for (Property p : properties) {
            output.append("\t" + p + "\n");
        }
        return output.toString();
    }

    @Override
    public String toString() {
        return generateDummyData();
    }


    public void generateError() {
        for (Property p : properties)
            p.generateError();
    }

    public void generateAnomaly() {
        for (Property p : properties)
            p.generateAnomaly();
    }

    public void normalizeFunctionality() {
        for (Property p : properties)
            p.resetToAcceptableValue();
    }

    public Set<Property> getProperties() {
        return properties;
    }

    public int getId() {
        return id;
    }
    public String getDescription() { return _description; }
}
