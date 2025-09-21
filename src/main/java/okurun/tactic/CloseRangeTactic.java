package okurun.tactic;

import java.util.List;

import dev.robocode.tankroyale.botapi.IBot;
import dev.robocode.tankroyale.botapi.graphics.Color;
import okurun.Commander;
import okurun.Util;
import okurun.driver.action.*;
import okurun.driver.handle.Handle;
import okurun.driver.handle.SwervingHandle;
import okurun.driver.trancemission.Trancemission;
import okurun.gunner.trigger.*;
import okurun.predictor.PredictData;
import okurun.predictor.Predictor;
import okurun.radaroperator.EnemyState;
import okurun.radaroperator.RadarOperator;

public class CloseRangeTactic extends AbstractTactic {
    public CloseRangeTactic(Commander commander) {
        super(commander);
    }

    @Override
    public Color getScanColor() {
        return Util.BASE_COLOR;
    }

    @Override
    public GunTrigger getNextGunTrigger() {
        return new QuickGunTrigger(commander);
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
            new SwervingHandle(commander, 16, 15),
            new SwervingHandle(commander, 4, 30)
        );
    }

    @Override
    public Trancemission getTrancemission() {
        return null;
    }
}
