package okurun.driver.trancemission;

import dev.robocode.tankroyale.botapi.Constants;
import dev.robocode.tankroyale.botapi.IBot;
import okurun.Commander;
import okurun.radaroperator.EnemyState;

public class TriggerShiftTransmission extends Trancemission {
    public TriggerShiftTransmission(Commander commander) {
        super(commander);
    }

    @Override
    public double changeGear(double speed) {
        final double enemyBulletPower = calcEnemyBulletPower();
        if (enemyBulletPower > 0) {
            return -speed;
        }
        return speed;
    }

    protected double calcEnemyBulletPower() {
        final EnemyState enemy = commander.getTargetEnemy();
        if (enemy == null) {
            return 0;
        }
        final IBot bot = commander.getBot();
        if (bot.getTurnNumber() - enemy.scandTurnNum > 1) {
            return 0;
        }
        if (enemy.previousState == null) {
            return 0;
        }
        final double diffEnargy = enemy.previousState.energy - enemy.energy;
        if (diffEnargy < 1) {
            return 0;
        }
        if (diffEnargy > Constants.MAX_FIREPOWER) {
            return 0;
        }
        return diffEnargy;
    }
}
