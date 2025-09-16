package okurun.gunner.trigger;

import okurun.Commander;
import okurun.radaroperator.EnemyState;

public abstract class AbstractTrigger implements GunTrigger {
    protected final Commander commander;

    protected AbstractTrigger(Commander commander) {
        this.commander = commander;
    }

    @Override
    public EnemyState getTargetEnemy() {
        return commander.getTargetEnemy();
    }
}
