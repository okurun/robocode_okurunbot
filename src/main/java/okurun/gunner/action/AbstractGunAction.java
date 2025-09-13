package okurun.gunner.action;

import okurun.Commander;
import okurun.gunner.ShootingTarget;
import okurun.gunner.trigger.Trigger;

public abstract class AbstractGunAction implements GunAction {
    protected final Commander commander;
    protected final Trigger trigger;

    protected ShootingTarget shootingTarget;

    protected AbstractGunAction(Commander commander, Trigger trigger) {
        this.commander = commander;
        this.trigger = trigger;
    }

    @Override
    public ShootingTarget getShootingTarget() {
        return shootingTarget;
    }
}
