package okurun.tactic;

import dev.robocode.tankroyale.botapi.Constants;
import dev.robocode.tankroyale.botapi.IBot;
import okurun.Commander;
import okurun.arenamap.ArenaMap;
import okurun.arenamap.ArenaMap.Wall;
import okurun.battlemanager.BattleManager;
import okurun.battlemanager.EnemyBattleData;
import okurun.driver.action.*;
import okurun.gunner.action.*;
import okurun.gunner.trigger.*;
import okurun.predictor.PredictData;
import okurun.predictor.Predictor;
import okurun.radaroperator.EnemyState;
import okurun.radaroperator.RadarOperator;
import okurun.radaroperator.action.*;

public abstract class AbstractTactic implements TacticStrategy {
    protected final Commander commander;

    protected AbstractTactic(Commander commander) {
        this.commander = commander;
    }

    @Override
    public void action() {
        commander.getRadarOperator().action();
        commander.getGunner().action();
        final DriveAction emergencyDriveAction = getEmergencyDriveAction();
        if (emergencyDriveAction != null) {
            commander.getDriver().setAction(emergencyDriveAction);
        }
        commander.getDriver().action();
    }

    @Override
    public int getTargetEnemyId() {
        final RadarOperator radarOperator = commander.getRadarOperator();
        final EnemyState enemy = radarOperator.getNearestEnemy();
        if (enemy != null) {
            return enemy.enemyId;
        }
        return 0;
    }

    @Override
    public RadarAction getNextRadarAction() {
        final IBot bot = commander.getBot();
        if (bot.getTurnNumber() <= 10) {
            return new ScanAroundRadarAction(commander);
        }
        final int targetEnemyId = commander.getTargetEnemyId();
        if (targetEnemyId != 0) {
            return new ScanTargetRadarAction(commander, targetEnemyId);
        }
        return new ScanAroundRadarAction(commander);
    }

    @Override
    public GunAction getNextGunAction() {
        return new NormalGunAction(commander);
    }

    @Override
    public GunTrigger getNextGunTrigger() {
        final EnemyState targetEnemy = commander.getTargetEnemy();
        if (targetEnemy == null) {
            return new QuickGunTrigger(commander);
        }
        final IBot bot = commander.getBot();
        final Predictor predictor = Predictor.getInstance();
        final PredictData pos = predictor.predict(targetEnemy, bot.getTurnNumber());
        final double distance;
        if (pos == null) {
            distance = bot.distanceTo(targetEnemy.x, targetEnemy.y);
        } else {
            distance = bot.distanceTo(pos.x, pos.y);
        }
        if (distance < 250) {
            return new QuickGunTrigger(commander);
        }

        int intervalTurnNum = 15;
        final int a = 3;
        if (distance < 350) {
            intervalTurnNum += a;
        }
        if (distance < 450) {
            intervalTurnNum += a;
        }
        if (distance < 550) {
            intervalTurnNum += a;
        }
        if (distance < 650) {
            intervalTurnNum += a;
        }

        final BattleManager battleManager = BattleManager.getInstance();
        final EnemyBattleData enemyBattleData = battleManager.getEnemyBattleData(targetEnemy.enemyId);
        if (enemyBattleData != null) {
            if (enemyBattleData.getTargetedCount() >= 10) {
                final double hitRate = enemyBattleData.getHitRate();
                if (hitRate < 0.4) {
                    intervalTurnNum += a;
                }
                if (hitRate < 0.3) {
                    intervalTurnNum += a;
                }
                if (hitRate < 0.2) {
                    intervalTurnNum += a;
                }
                if (hitRate < 0.1) {
                    intervalTurnNum += a;
                }
            }
        }

        final double energy = bot.getEnergy();
        if (energy < 40) {
            intervalTurnNum += a;
        }
        if (energy < 30) {
            intervalTurnNum += a;
        }
        if (energy < 20) {
            intervalTurnNum += a;
        }
        if (energy < 10) {
            intervalTurnNum += a;
        }

        return new PeriodicGunTrigger(commander, intervalTurnNum);
    }

    @Override
    public DriveAction getNextDriveAction() {
        return new ApproachEnemyDriveAction(commander, commander.getTargetEnemy(), 300);
    }

    protected DriveAction getEmergencyDriveAction() {
        final IBot bot = commander.getBot();
        final ArenaMap arenaMap = ArenaMap.getInstance();
        final Wall nearestWall = arenaMap.getNearestWall(bot);
        final double distanceToWall = nearestWall.distanceTo(bot);
        if (distanceToWall < (Commander.BODY_SIZE / 2) + (Constants.MAX_SPEED * 4) + 5) {
            return new AvoidWallDriveAction(commander);
        }
        return null;
    }
}
