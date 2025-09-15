package okurun.gunner.action;

import dev.robocode.tankroyale.botapi.IBot;
import okurun.Commander;
import okurun.gunner.ShootingTarget;
import okurun.gunner.trigger.Trigger;
import okurun.predictor.PredictData;
import okurun.predictor.Predictor;
import okurun.radaroperator.EnemyState;

public class NormalGunAction extends AbstractGunAction {
    public NormalGunAction(Commander commander, Trigger trigger) {
        super(commander, trigger);
    }

    @Override
    public GunAction action() {
        final EnemyState targetEnemy = trigger.getTargetEnemy();
        if (targetEnemy == null) {
            shootingTarget = null;
            return null;
        }
        if (targetEnemy.energy <= 0) {
            final IBot bot = commander.getBot();
            final double distance = bot.distanceTo(targetEnemy.x, targetEnemy.y);
            final double firePower = trigger.getFirePower();
            shootingTarget = new ShootingTarget(
                targetEnemy.enemyId,
                firePower,
                targetEnemy.x, targetEnemy.y,
                distance,
                bot.getTurnNumber() + (int) Math.ceil(distance / bot.calcBulletSpeed(firePower)),
                null
            );
            System.out.println("####");
            return null;
        }
        shootingTarget = createShootingTarget();
        return null;
    }

    private ShootingTarget createShootingTarget() {
        final IBot bot = commander.getBot();
        final EnemyState targetEnemy = trigger.getTargetEnemy();
        final int nextFireTurnNum = trigger.getNextFireTurnNum();
        final double firePower = trigger.getFirePower();
        final Predictor predictor = Predictor.getInstance();
        final double bulletSpeed = bot.calcBulletSpeed(firePower);
        double distance = 0;
        PredictData predictData = null;
        for (int i = 1; i < 50; i++) {
            predictData = predictor.predict(targetEnemy, nextFireTurnNum + i);
            distance = bot.distanceTo(predictData.x, predictData.y);
            if (Math.abs(distance - (bulletSpeed * i)) < 10) {
                System.out.println("%%%%");
                return new ShootingTarget(
                    targetEnemy.enemyId,
                    firePower,
                    predictData.x, predictData.y,
                    distance,
                    nextFireTurnNum + i,
                    predictData.model
                );
            }
        }
        return null;
    };
}
