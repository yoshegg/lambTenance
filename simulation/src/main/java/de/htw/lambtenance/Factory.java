package de.htw.lambtenance;

import de.htw.lambtenance.machines.*;
import de.htw.lambtenance.properties.Property;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;

public class Factory {

    private static final int NUMBER_OF_MACHINES = 100;

    private Set<Machine> machines = new HashSet<>();

    private Set<Machine> erroneousMachines = new HashSet<>();
    private Set<Machine> abnormalMachines = new HashSet<>();

    private List<Class> classesOfMachines = new ArrayList<>() {{
        add(Rolling.class);
        add(Painter.class);
        add(PunchingMachine.class);
        add(Oven.class);
        add(Lighting.class);
        add(RoomAirDistribution.class);
        add(ConveyorBelt.class);
    }};

    private Thread machineThread;


    public Factory() {
        generateMachines();
    }

    public void start() {
        startMachines();
    }

    public void stop() {
        stopMachines();
    }

    private void generateMachines() {
        for (int i = 1; i <= NUMBER_OF_MACHINES; i++) {
            int numberOfClassesOfMachines = classesOfMachines.size();
            int randomMachineIndex = new Random().nextInt(numberOfClassesOfMachines);
            Class<?> c = classesOfMachines.get(randomMachineIndex);
            try {
                Machine m = (Machine) c.newInstance();
                machines.add(m);
            } catch (Exception e) {
                System.out.println(c.getName() + " could not be initialized.");
            }
        }

    }

    private void startMachines() {
        Thread thread = new Thread(() -> {
        machineThread = new Thread(() -> {
            Thread thisThread = Thread.currentThread();
            try {
                FileWriter fileWriter = new FileWriter("test.txt");
                BufferedWriter out = new BufferedWriter(fileWriter);
                while (machineThread == thisThread) {
                    for (Machine m : machines)
                        out.write(m.toString());
                    out.flush();
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        });
        machineThread.start();
    }
    }

    public void generatePropertyError() {
        Machine randomMachine = getRandomMachine(machines);
        Property randomProperty = getRandomProperty(randomMachine);
        randomProperty.generateError();
        System.out.println(randomMachine);
    }

    public void generatePropertyAnomaly() {
        Machine randomMachine = getRandomMachine(machines);
        Property randomProperty = getRandomProperty(randomMachine);
        randomProperty.generateAnomaly();
        System.out.println(randomMachine);
    }

    public Machine generateMachineError() {
        Machine randomMachine;
        do {
            randomMachine = getRandomMachine(machines);
        } while (erroneousMachines.contains(randomMachine));
        randomMachine.generateError();
        erroneousMachines.add(randomMachine);
        System.out.println(randomMachine);
        return randomMachine;
    }

    public Machine generateMachineAnomaly() {
        Machine randomMachine;
        do {
            randomMachine = getRandomMachine(machines);
        } while (abnormalMachines.contains(randomMachine));
        randomMachine.generateAnomaly();
        abnormalMachines.add(randomMachine);
        System.out.println(randomMachine);
        return randomMachine;
    }

    /**
     * "Repairs" all the machines.
     */
    public void normalizeMachines() {
        for (Machine m : erroneousMachines)
            m.normalizeFunctionality();
        erroneousMachines.clear();
        for (Machine m : abnormalMachines)
            m.normalizeFunctionality();
        abnormalMachines.clear();
    }

    private Machine getRandomMachine(Set<Machine> ms) {
        int numberOfMachines = ms.size();
        int rndIndex = new Random().nextInt(numberOfMachines);
        return (Machine) ms.toArray()[rndIndex];
    }

    private Property getRandomProperty(Machine m) {
        int numberOfProperties = m.getProperties().size();
        int rndIndex = new Random().nextInt(numberOfProperties);
        return (Property) m.getProperties().toArray()[rndIndex];
    }

}
