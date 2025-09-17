package okurun.gunner.trigger;

import dev.robocode.tankroyale.botapi.graphics.Color;
import okurun.radaroperator.EnemyState;

public interface GunTrigger {
    double getFirePower();
    int getNextFireTurnNum();
    EnemyState getTargetEnemy();
    Color getGunColor();
}
