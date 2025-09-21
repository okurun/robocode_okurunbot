package okurun.tactic;

import java.util.Comparator;
import java.util.List;

import dev.robocode.tankroyale.botapi.IBot;
import dev.robocode.tankroyale.botapi.graphics.Color;
import okurun.Commander;
import okurun.Util;
import okurun.arenamap.ArenaMap;
import okurun.arenamap.ArenaMap.Corner;
import okurun.battlemanager.BattleManager;
import okurun.battlemanager.EnemyBattleData;
import okurun.driver.Driver;
import okurun.driver.action.*;
import okurun.driver.handle.*;
import okurun.driver.trancemission.*;
import okurun.gunner.Gunner;
import okurun.gunner.ShootingTarget;
import okurun.gunner.trigger.*;
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
    public List<Handle> getHandles() {
        return List.of(
            new SwervingHandle(commander, 16, 15),
            new SwervingHandle(commander, 4, 30)
        );
    }

    @Override
    public Trancemission getTrancemission() {
        final EnemyState targetEnemy = commander.getTargetEnemy();
        if (targetEnemy != null) {
            final IBot bot = commander.getBot();
            if (bot.getEnergy() - targetEnemy.energy > 20) {
                return null;
            }

            final Predictor predictor = Predictor.getInstance();
            final PredictData predictData = predictor.predict(targetEnemy, bot.getTurnNumber());
            final double distanceToEnemy;
            if (predictData != null) {
                distanceToEnemy = bot.distanceTo(predictData.x, predictData.y);
            } else {
                distanceToEnemy = bot.distanceTo(targetEnemy.x, targetEnemy.y);
            }
            if (bot.getEnergy() - targetEnemy.energy < -20) {
                if (distanceToEnemy < 200) {
                    return null;
                }
            }
        }

        final Driver driver = commander.getDriver();
        if (driver.getAction() instanceof SideMoveDriveAction) {
            return new PeriodicTrancemission(commander, 10, 10);
        }
        return new RandomTrancemission(commander, 9, 1);
    }

    @Override
    public Color getScanColor() {
        return Util.LIGHT_YELLOW_COLOR;
    }
}
