package okurun.gunner.trigger;

import okurun.Commander;
import okurun.radaroperator.EnemyState;

public abstract class AbstractTrigger implements Trigger {
    protected final Commander commander;
    protected final EnemyState targetEnemy;

    protected AbstractTrigger(Commander commander, EnemyState targetEnemy) {
        this.commander = commander;
        this.targetEnemy = targetEnemy;
    }

    @Override
    public EnemyState getTargetEnemy() {
        return this.targetEnemy;
    }
}
