package okurun.gunner.trigger;

import okurun.radaroperator.EnemyState;

public interface Trigger {
    double getFirePower();
    int getNextFireTurnNum();
    EnemyState getTargetEnemy();
}
