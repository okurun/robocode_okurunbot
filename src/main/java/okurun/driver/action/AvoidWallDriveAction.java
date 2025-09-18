package okurun.driver.action;

import dev.robocode.tankroyale.botapi.Constants;
import dev.robocode.tankroyale.botapi.IBot;
import dev.robocode.tankroyale.botapi.graphics.Color;
import okurun.Commander;
import okurun.Util;
import okurun.Util.Direction;
import okurun.arenamap.ArenaMap;
import okurun.arenamap.ArenaMap.Wall;
import okurun.predictor.PredictData;
import okurun.predictor.Predictor;
import okurun.radaroperator.EnemyState;
import okurun.radaroperator.RadarOperator;

public class AvoidWallDriveAction extends AbstractDriveAction {

    public AvoidWallDriveAction(Commander commander) {
        super(commander);
    }

    @Override
    public DriveAction action() {
        final IBot bot = commander.getBot();
        final ArenaMap arenaMap = ArenaMap.getInstance();
        final Wall nearestWall = arenaMap.getNearestWall(bot);
        final double wallDegree = nearestWall.degreeTo(bot);
        double turnDegree;
        double forwardDistance = 30;
        double speed = Constants.MAX_SPEED;
        final double wallEscapeAngleOffset = 30;
        if (wallDegree == 0) {
            turnDegree = 90 + wallEscapeAngleOffset;
        } else {
            final Direction direction = (wallDegree < 0) ? Direction.LEFT : Direction.RIGHT;
            if (bot.getSpeed() < 0) {
                forwardDistance = -forwardDistance;
                turnDegree = bot.normalizeRelativeAngle((90 + Math.abs(wallDegree) + wallEscapeAngleOffset) * direction.value);
            } else {
                turnDegree = bot.normalizeRelativeAngle((90 - Math.abs(wallDegree) + wallEscapeAngleOffset) * direction.value);
            }
        }

        final RadarOperator radarOperator = commander.getRadarOperator();
        final EnemyState enemy = radarOperator.getNearestEnemy();
        if (enemy != null) {
            final Predictor predictor = Predictor.getInstance();
            final PredictData predictedEnemy = predictor.predict(enemy, bot.getTurnNumber());
            if (predictedEnemy != null) {
                final double distanceToEnemy = bot.distanceTo(predictedEnemy.x, predictedEnemy.y);
                if (distanceToEnemy < 100) {
                    final double degreeToEnemy = bot.bearingTo(predictedEnemy.x, predictedEnemy.y);
                    if (forwardDistance > 0 && Math.abs(degreeToEnemy) < 45) {
                        forwardDistance = -forwardDistance;
                        turnDegree = -turnDegree;
                    } else if (forwardDistance < 0 && Math.abs(degreeToEnemy) > 135) {
                        forwardDistance = -forwardDistance;
                        turnDegree = -turnDegree;
                    }
                }
            }
        }
        this.forwardDistance = forwardDistance;
        this.turnDegree = turnDegree;
        this.targetSpeed = speed;

        if (nearestWall.distanceTo(bot) < 60) {
            return this;
        }
        return null;
    }

    @Override
    public Color getTracksColor() {
        return Util.YELLOW_COLOR;
    }
}