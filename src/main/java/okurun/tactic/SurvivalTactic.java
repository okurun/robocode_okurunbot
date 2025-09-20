package okurun.tactic;

import java.util.Comparator;
import java.util.List;

import dev.robocode.tankroyale.botapi.IBot;
import okurun.Commander;
import okurun.arenamap.ArenaMap;
import okurun.arenamap.ArenaMap.Corner;
import okurun.driver.action.*;
import okurun.gunner.Gunner;
import okurun.gunner.ShootingTarget;
import okurun.predictor.PredictData;
import okurun.predictor.Predictor;
import okurun.radaroperator.EnemyState;
import okurun.radaroperator.RadarOperator;
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

    @Override
    public DriveAction getNextDriveAction() {
        final IBot bot = commander.getBot();
 
        final ArenaMap arenaMap = ArenaMap.getInstance();
        final Corner nearestCorner = arenaMap.getNearestCorner(bot);
        final RadarOperator radarOperator = commander.getRadarOperator();
        final Predictor predictor = Predictor.getInstance();
        final List<double[]> enemies = radarOperator.getEnemyStates().values().stream()
            .map(e -> {
                PredictData pd = predictor.predict(e, bot.getTurnNumber());
                return pd != null ? pd.getPosition() : new double[] {e.x, e.y};
            })
            .toList();
        final double safeAreaDistance = Math.min(arenaMap.getWidth(), arenaMap.getHeight());
        final Corner safeCorner = nearestCorner.getNeighboringCornersWithSelf().stream()
            .min(Comparator.comparingInt(c -> {
                return (int) enemies.stream()
                    .filter(e -> c.distanceTo(e) < safeAreaDistance)
                    .count();
            }))
            .orElse(null);
        if (safeCorner.id == nearestCorner.id) {
            final EnemyState targetEnemy = commander.getTargetEnemy();
            if (targetEnemy != null) {
                return new SideMoveDriveAction(commander, targetEnemy, 400);
            }
        }
        final double[] targetPoint = switch (safeCorner.id) {
            case TOP_LEFT -> new double[] {100, arenaMap.getHeight() - 100};
            case TOP_RIGHT -> new double[] {arenaMap.getWidth() - 100, arenaMap.getHeight() - 100};
            case BOTTOM_RIGHT -> new double[] {arenaMap.getWidth() - 100, 100};
            case BOTTOM_LEFT -> new double[] {100, 100};
        };
        return new TargetPointDriveAction(commander, targetPoint);
    }
}
