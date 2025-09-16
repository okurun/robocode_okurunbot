package okurun.gunner.trigger;

import okurun.radaroperator.EnemyState;

public interface GunTrigger {
    double getFirePower();
    int getNextFireTurnNum();
    EnemyState getTargetEnemy();
}
