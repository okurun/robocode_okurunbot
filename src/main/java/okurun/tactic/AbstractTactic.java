package okurun.tactic;

import dev.robocode.tankroyale.botapi.IBot;
import okurun.Commander;
import okurun.arenamap.ArenaMap;
import okurun.arenamap.ArenaMap.Wall;
import okurun.driver.action.*;
import okurun.gunner.action.*;
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

    protected DriveAction getEmergencyDriveAction() {
        final IBot bot = commander.getBot();
        final ArenaMap arenaMap = ArenaMap.getInstance();
        final Wall nearestWall = arenaMap.getNearestWall(bot);
        final double distanceToWall = nearestWall.distanceTo(bot);
        if (distanceToWall < 45) {
            return new AvoidWallDriveAction(commander);
        }
        return null;
    }
}
