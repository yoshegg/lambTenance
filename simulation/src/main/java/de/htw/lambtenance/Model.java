package de.htw.lambtenance;

public class Model {

    private Factory _factory = new Factory();

    public void startFactory() {
        _factory.start();
    }

    public Factory getFactory() { return _factory; }
}
