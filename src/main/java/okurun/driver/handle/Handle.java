package okurun.driver.handle;

import okurun.Commander;

public abstract class Handle {
    protected final Commander commander;

    protected Handle(Commander commander) {
        this.commander = commander;
    }

    public abstract double handle(double turnDegree);
}
