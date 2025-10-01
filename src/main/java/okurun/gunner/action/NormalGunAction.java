package okurun.gunner.action;

import dev.robocode.tankroyale.botapi.Constants;
import dev.robocode.tankroyale.botapi.IBot;
import okurun.Commander;
import okurun.gunner.ShootingTarget;
import okurun.gunner.trigger.GunTrigger;
import okurun.predictor.PredictData;
import okurun.predictor.Predictor;
import okurun.radaroperator.EnemyState;

public class NormalGunAction extends AbstractGunAction {
    public NormalGunAction(Commander commander) {
        super(commander);
    }

    @Override
    public GunAction action(GunTrigger trigger) {
        final EnemyState targetEnemy = trigger.getTargetEnemy();
        if (targetEnemy == null) {
            shootingTarget = null;
            return null;
        }
        if (targetEnemy.energy <= 0) {
            // 動けなくなっているのでスキャンした位置に向かって撃つ
            final IBot bot = commander.getBot();
            final double distance = bot.distanceTo(targetEnemy.x, targetEnemy.y);
            final double firePower = trigger.getFirePower();
            shootingTarget = new ShootingTarget(
                targetEnemy.enemyId,
                firePower,
                targetEnemy.x, targetEnemy.y,
                distance,
                bot.getTurnNumber() + (int) Math.ceil(distance / bot.calcBulletSpeed(firePower)),
                trigger.getNextFireTurnNum(),
                null
            );
            return null;
        }
        shootingTarget = createShootingTarget(trigger);
        return null;
    }

    private ShootingTarget createShootingTarget(GunTrigger trigger) {
        final IBot bot = commander.getBot();
        final EnemyState targetEnemy = trigger.getTargetEnemy();
        final int nextFireTurnNum = trigger.getNextFireTurnNum();
        final double firePower = trigger.getFirePower();
        final Predictor predictor = Predictor.getInstance();
        final double bulletSpeed = bot.calcBulletSpeed(Math.max(firePower, Constants.MIN_FIREPOWER));
        double distance = 0;
        PredictData predictData = null;
        // １ターンづつ弾が当たるかシミュレーションして当たる位置を返す
        for (int i = 1; i < 70; i++) {
            predictData = predictor.predict(targetEnemy, nextFireTurnNum + i);
            if (predictData == null) {
                return null;
            }
            distance = bot.distanceTo(predictData.x, predictData.y);
            if (distance - (bulletSpeed * i) < 0) {
                return new ShootingTarget(
                    targetEnemy.enemyId,
                    firePower,
                    predictData.x, predictData.y,
                    distance,
                    nextFireTurnNum + i,
                    nextFireTurnNum,
                    predictData.model
                );
            }
        }
        return null;
    };
}
