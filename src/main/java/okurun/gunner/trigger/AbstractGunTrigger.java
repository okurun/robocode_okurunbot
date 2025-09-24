package okurun.gunner.trigger;

import dev.robocode.tankroyale.botapi.Constants;
import dev.robocode.tankroyale.botapi.IBot;
import okurun.Commander;
import okurun.predictor.PredictData;
import okurun.predictor.Predictor;
import okurun.radaroperator.EnemyState;

public abstract class AbstractGunTrigger implements GunTrigger {
    protected final Commander commander;
    private int nextFireTurnNum = 0;

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
        if (bot.getTurnNumber() >= nextFireTurnNum) {
            return calcFirePowoer();
        }
        return 0;
    }

    private double calcFirePowoer() {
        final EnemyState targetEnemy = getTargetEnemy();
        if (targetEnemy.energy <= 0) {
            return Constants.MIN_FIREPOWER;
        }
        final IBot bot = commander.getBot();
        final Predictor predictor = Predictor.getInstance();
        final PredictData currentPos = predictor.predict(targetEnemy, bot.getTurnNumber());
        if (currentPos == null) {
            return 0;
        }
        final double distance = bot.distanceTo(currentPos.x, currentPos.y);

        double firePower;
        if (distance < 50) {
            firePower = Constants.MAX_FIREPOWER;
        } else if (distance < 150) {
            firePower = 2.5;
        } else if (distance < 250) {
            firePower = 2;
        } else if (distance < 350) {
            firePower = 1.5;
        } else {
            firePower = 1;
        }
        final double prevDistance = bot.distanceTo(targetEnemy.x, targetEnemy.y);
        final double diffDistance = distance - prevDistance;
        final int diffTurnNum = bot.getTurnNumber() - targetEnemy.scandTurnNum;
        final double diffDistancePerTurn;
        if (Math.abs(diffDistance) > 0) {
            diffDistancePerTurn = diffDistance / diffTurnNum;
        } else {
            diffDistancePerTurn = 0;
        }
        if (diffDistancePerTurn < 0) {
            // 近づいて来ている場合は火力を上げる
            if (diffDistancePerTurn < Constants.MAX_SPEED * 0.75) {
                firePower += 1.5;
            } else if (diffDistancePerTurn < -Constants.MAX_SPEED * 0.5) {
                firePower += 1;
            } else if (diffDistancePerTurn < -Constants.MAX_SPEED * 0.25) {
                firePower += 0.5;
            }
        } else {
            // 離れて行ってる場合は火力を下げる
            if (diffDistancePerTurn > Constants.MAX_SPEED * 0.75) {
                firePower -= 1.5;
            } else if (diffDistancePerTurn > Constants.MAX_SPEED * 0.5) {
                firePower -= 1;
            } else if (diffDistancePerTurn > Constants.MAX_SPEED * 0.25) {
                firePower -= 0.5;
            }
        }
        return Math.min(Math.max(firePower, Constants.MIN_FIREPOWER), Constants.MAX_FIREPOWER);
    }

    @Override
    public int getNextFireTurnNum() {
        if (nextFireTurnNum == 0) {
            nextFireTurnNum = calcNextFireTurnNum();
        }
        return nextFireTurnNum;
    }

    protected abstract int calcNextFireTurnNum();
}
