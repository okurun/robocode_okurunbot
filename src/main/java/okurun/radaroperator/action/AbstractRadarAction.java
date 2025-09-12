package okurun.radaroperator.action;

import okurun.Commander;

public abstract class AbstractRadarAction implements RadarAction {
    protected final Commander commander;
    protected double turnRadarDegree = 0;

    protected AbstractRadarAction(Commander commander) {
        this.commander = commander;
    }

    @Override
    public double getTurnRadarDegree() {
        return turnRadarDegree;
    }
}
