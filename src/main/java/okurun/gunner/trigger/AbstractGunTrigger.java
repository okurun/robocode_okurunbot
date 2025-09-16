package okurun.gunner.trigger;

import dev.robocode.tankroyale.botapi.Constants;
import dev.robocode.tankroyale.botapi.IBot;
import okurun.Commander;
import okurun.predictor.PredictData;
import okurun.predictor.Predictor;
import okurun.radaroperator.EnemyState;

public abstract class AbstractGunTrigger implements GunTrigger {
    protected final Commander commander;

    protected AbstractGunTrigger(Commander commander) {
        this.commander = commander;
    }

    @Override
    public EnemyState getTargetEnemy() {
        return commander.getTargetEnemy();
    }

    @Override
    public double getFirePower() {
        final IBot bot = commander.getBot();
        final int nextFireTurnNum = getNextFireTurnNum();
        if (bot.getTurnNumber() <= nextFireTurnNum) {
            return calcFirePowoer();
        }
        return 0;
    }

    private double calcFirePowoer() {
        final EnemyState targetEnemy = getTargetEnemy();
        if (targetEnemy.energy <= 0) {
            return Constants.MAX_FIREPOWER;
        }
        final IBot bot = commander.getBot();
        final Predictor predictor = Predictor.getInstance();
        final PredictData currentPos = predictor.predict(targetEnemy, bot.getTurnNumber());
        if (currentPos == null) {
            return 0;
        }
        final double distance = bot.distanceTo(currentPos.x, currentPos.y);
        if (distance < 90) {
            return Constants.MAX_FIREPOWER;
        }
        if (distance > 400) {
            return 1;
        }
        if (distance > 550) {
            return 0;
        }

        double firePower = 2;
        if (distance > 250) {
            firePower -= 1;
        }
        final double prevDistance = bot.distanceTo(targetEnemy.x, targetEnemy.y);
        final double diffDistance = distance - prevDistance;
        final int diffTurnNum = bot.getTurnNumber() - targetEnemy.scandTurnNum;
        final double diffDistancePerTurn;
        if (Math.abs(diffDistance) > 1) {
            diffDistancePerTurn = diffDistance / diffTurnNum;
        } else {
            diffDistancePerTurn = 0;
        }
        if (diffDistancePerTurn < Constants.MAX_SPEED / 2 * -1) {
            firePower += 1;
        } else if (diffDistancePerTurn > Constants.MAX_SPEED / 2) {
            firePower -= 1;
        }
        return Math.max(firePower, 1);
    }
}
