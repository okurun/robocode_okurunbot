package okurun.gunner.action;

import okurun.gunner.ShootingTarget;

public interface GunAction {

    GunAction action();

    ShootingTarget getShootingTarget();
}
