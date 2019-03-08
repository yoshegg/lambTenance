package de.htw.lambtenance;

public class Model {

    private Factory _factory = new Factory();

    public void startFactory() {
        _factory.start();
    }

    public void stopFactory() { _factory.stop(); }

    public Factory getFactory() { return _factory; }
}
