package okurun.radaroperator.action;

import okurun.Commander;

public class ScanAroundRadarAction extends AbstractRadarAction {
    public ScanAroundRadarAction(Commander commander) {
        super(commander);
    }

    @Override
    public RadarAction action() {
        turnRadarDegree = 360;
        return null;
    }
}
