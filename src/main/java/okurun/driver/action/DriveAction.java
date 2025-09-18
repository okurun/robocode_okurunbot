package okurun.driver.action;

import dev.robocode.tankroyale.botapi.graphics.Color;

public interface DriveAction {

    DriveAction action();

    double getTurnDegree();

    double getForwardDistance();

    double getTargetSpeed();

    Color getTracksColor();
}
