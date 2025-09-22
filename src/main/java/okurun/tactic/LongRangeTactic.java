package okurun.tactic;

import java.util.List;

import dev.robocode.tankroyale.botapi.IBot;
import dev.robocode.tankroyale.botapi.graphics.Color;
import okurun.Commander;
import okurun.Util;
import okurun.battlemanager.BattleManager;
import okurun.battlemanager.EnemyBattleData;
import okurun.driver.Driver;
import okurun.driver.action.ApproachDriveAction;
import okurun.driver.action.ChargeDriveAction;
import okurun.driver.action.DriveAction;
import okurun.driver.action.EscapeDriveAction;
import okurun.driver.action.SideMoveDriveAction;
import okurun.driver.handle.Handle;
import okurun.driver.handle.RandmoSwervingHandle;
import okurun.driver.handle.SwervingHandle;
import okurun.driver.trancemission.PeriodicTrancemission;
import okurun.driver.trancemission.RandomTrancemission;
import okurun.driver.trancemission.Trancemission;
import okurun.gunner.trigger.GunTrigger;
import okurun.gunner.trigger.PeriodicGunTrigger;
import okurun.gunner.trigger.QuickGunTrigger;
import okurun.predictor.PredictData;
import okurun.predictor.Predictor;
import okurun.radaroperator.EnemyState;
import okurun.radaroperator.RadarOperator;

public class LongRangeTactic extends AbstractTactic {
    public LongRangeTactic(Commander commander) {
        super(commander);
    }

    @Override
    public Color getScanColor() {
        return Util.WHITE_COLOR;
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
        if (distance < 500) {
            intervalTurnNum += a;
        }
        if (distance < 550) {
            intervalTurnNum += a;
        }
        if (distance < 600) {
            intervalTurnNum += a;
        }
        if (distance < 650) {
            intervalTurnNum += a;
        }
        if (distance < 700) {
            intervalTurnNum += a;
        }
        if (distance < 750) {
            intervalTurnNum += a;
        }

        final BattleManager battleManager = BattleManager.getInstance();
        final EnemyBattleData enemyBattleData = battleManager.getEnemyBattleData(targetEnemy.enemyId);
        if (enemyBattleData != null) {
            if (enemyBattleData.getTargetedCount() >= 10) {
                final double hitRate = enemyBattleData.getHitRate();
                if (hitRate < 0.5) {
                    intervalTurnNum += a;
                }
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
        if (energy < 50) {
            intervalTurnNum += a;
        }
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
        final EnemyState targetEnemy = commander.getTargetEnemy();
        if (targetEnemy != null) {
            final IBot bot = commander.getBot();
            if (bot.getEnergy() - targetEnemy.energy > 20) {
                return new ChargeDriveAction(commander, targetEnemy);
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
                    return new EscapeDriveAction(commander, targetEnemy);
                }
                if (distanceToEnemy > 400) {
                    return new SideMoveDriveAction(commander, targetEnemy, 400);
                }
            }
            if (bot.getEnergy() - targetEnemy.energy < 0) {
                return new SideMoveDriveAction(commander, targetEnemy, 300);
            }
        }
        return new ApproachDriveAction(commander, targetEnemy, 300);
    }

    @Override
    protected DriveAction getEmergencyDriveAction() {
        final DriveAction action = super.getEmergencyDriveAction();
        if (action != null) {
            return action;
        }

        final IBot bot = commander.getBot();
        final RadarOperator radarOperator = commander.getRadarOperator();
        final EnemyState nearestEnemy = radarOperator.getNearestEnemy();
        if (nearestEnemy != null) {
            if (bot.getEnergy() - nearestEnemy.energy < 20) {
                final Predictor predictor = Predictor.getInstance();
                final PredictData predictData = predictor.predict(nearestEnemy, bot.getTurnNumber());
                final double distanceToEnemy;
                if (predictData != null) {
                    distanceToEnemy = bot.distanceTo(predictData.x, predictData.y);
                } else {
                    distanceToEnemy = bot.distanceTo(nearestEnemy.x, nearestEnemy.y);
                }
                if (distanceToEnemy < 80) {
                    return new EscapeDriveAction(commander, nearestEnemy);
                }
            }
        }
        return null;
    }

    @Override
    public List<Handle> getHandles() {
        return List.of(
            new SwervingHandle(commander, 16, 30),
            new RandmoSwervingHandle(commander, 15)
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
        }

        final Driver driver = commander.getDriver();
        if (driver.getAction() instanceof SideMoveDriveAction) {
            return new PeriodicTrancemission(commander, 10, 10);
        }
        return new RandomTrancemission(commander, 9, 1);
    }
}
