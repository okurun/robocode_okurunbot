package okurun.driver.action;

import okurun.Commander;

public abstract class AbstractDriveAction implements DriveAction {
    protected final Commander commander;

    protected double turnDegree;
    protected double forwardDistance;
    protected double targetSpeed;

    protected AbstractDriveAction(Commander commander) {
        this.commander = commander;
    }

    @Override
    public double getTurnDegree() {
        return turnDegree;
    }

    @Override
    public double getForwardDistance() {
        return forwardDistance;
    }

    @Override
    public double getTargetSpeed() {
        return targetSpeed;
    }
}
