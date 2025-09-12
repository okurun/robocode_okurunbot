package okurun.gunner.action;

import okurun.Commander;
import okurun.gunner.ShootingTarget;

public abstract class AbstractGunAction implements GunAction {
    protected final Commander commander;

    protected ShootingTarget shootingTarget;

    protected AbstractGunAction(Commander commander) {
        this.commander = commander;
    }

    @Override
    public ShootingTarget getShootingTarget() {
        return shootingTarget;
    }
}
