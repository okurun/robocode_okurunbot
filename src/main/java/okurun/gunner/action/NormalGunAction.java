package okurun.gunner.action;

import dev.robocode.tankroyale.botapi.IBot;
import okurun.Commander;
import okurun.gunner.ShootingTarget;
import okurun.predictor.PredictData;
import okurun.predictor.Predictor;
import okurun.predictor.Util;
import okurun.radaroperator.EnemyState;

public class NormalGunAction extends AbstractGunAction {
    private final EnemyState targetEnemy;
    private final double firePower;

    public NormalGunAction(Commander commander, EnemyState targetEnemy, double firePower) {
        super(commander);
        this.targetEnemy = targetEnemy;
        this.firePower = firePower;
    }

    @Override
    public GunAction action() {
        if (targetEnemy == null) {
            shootingTarget = null;
            return null;
        }
        shootingTarget = createShootingTarget();
        return null;
    }

    private ShootingTarget createShootingTarget() {
        final IBot bot = commander.getBot();
        final Predictor predictor = Predictor.getInstance();
        final double bulletSpeed = bot.calcBulletSpeed(firePower);
        final int nextFireTurnNum = Util.calcNextFireTurnNum(bot.getGunHeat(), bot.getGunCoolingRate()) + bot.getTurnNumber();
        double distance = 0;
        PredictData predictData = null;
        for (int i = 1; i < 50; i++) {
            predictData = predictor.predict(targetEnemy, nextFireTurnNum + i);
            distance = bot.distanceTo(predictData.x, predictData.y);
            if (Math.abs(distance - (bulletSpeed * i)) < 10) {
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
