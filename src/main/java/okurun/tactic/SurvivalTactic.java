package okurun.tactic;

import dev.robocode.tankroyale.botapi.IBot;
import okurun.Commander;
import okurun.gunner.Gunner;
import okurun.gunner.ShootingTarget;
import okurun.radaroperator.EnemyState;
import okurun.radaroperator.action.*;

public class SurvivalTactic extends AbstractTactic {

    public SurvivalTactic(Commander commander) {
        super(commander);
    }

    @Override
    public RadarAction getNextRadarAction() {
        final EnemyState enemy = commander.getTargetEnemy();
        if (enemy == null) {
            return new ScanAroundRadarAction(commander);
        }
        final IBot bot = commander.getBot();
        final Gunner gunner = commander.getGunner();
        final ShootingTarget shootingTarget = gunner.getShootingTarget(bot.getTurnNumber() - 1);
        if (shootingTarget == null) {
            return new ScanAroundRadarAction(commander);
        }
        final int fireTurn = shootingTarget.fireTurnNum;
        if (fireTurn - bot.getTurnNumber() > 5) {
            return new ScanAroundRadarAction(commander);
        }
        return new ScanTargetRadarAction(commander, enemy.enemyId);
    }
}
