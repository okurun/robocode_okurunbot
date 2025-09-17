package okurun.driver.action;

public interface DriveAction {

    DriveAction action();

    double getTurnDegree();

    double getForwardDistance();

    double getTargetSpeed();
}
