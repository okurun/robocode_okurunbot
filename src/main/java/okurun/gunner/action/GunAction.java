package okurun.gunner.action;

import okurun.gunner.ShootingTarget;
import okurun.gunner.trigger.GunTrigger;

public interface GunAction {

    GunAction action(GunTrigger trigger);

    ShootingTarget getShootingTarget();
}
