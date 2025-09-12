package okurun.tactic;

import okurun.Commander;
import okurun.driver.action.DriveAction;

public class MiddleRangeTactic extends AbstractTactic {
    public MiddleRangeTactic(Commander commander) {
        super(commander);
    }

    @Override
    protected DriveAction getEmergencyDriveAction() {
        return null;
    }
}
