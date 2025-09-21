package okurun.driver.action;

import dev.robocode.tankroyale.botapi.Constants;
import dev.robocode.tankroyale.botapi.graphics.Color;
import okurun.Commander;
import okurun.Util;

public class TargetPointDriveAction extends AbstractDriveAction {
    private final double[] targetPoint;

    public TargetPointDriveAction(Commander commander, double[] targetPoint) {
        super(commander);
        this.targetPoint = targetPoint;
    }

    @Override
    public DriveAction action() {
        this.targetSpeed = Constants.MAX_SPEED;
        this.turnDegree = commander.getBot().bearingTo(targetPoint[0], targetPoint[1]);
        return null;
    }

    @Override
    public Color getTracksColor() {
        return Util.DARK_COLOR;
    }
}
