package okurun.driver.brake;

import okurun.Commander;

public abstract class Brake {
    protected final Commander commander;

    protected Brake(Commander commander) {
        this.commander = commander;
    }

    public abstract double brake(double speed, double forwardDistance);
}
