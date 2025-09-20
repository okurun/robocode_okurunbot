package okurun.driver.trancemission;

import okurun.Commander;

public abstract class Trancemission {
    protected final Commander commander;

    protected Trancemission(Commander commander) {
        this.commander = commander;
    }

    public abstract double changeGear(double speed);
}
