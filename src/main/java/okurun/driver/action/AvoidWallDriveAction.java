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

/**
 * 壁から離れるDriveAction
 */
public class AvoidWallDriveAction extends AbstractDriveAction {

    public AvoidWallDriveAction(Commander commander) {
        super(commander);
    }

    @Override
    public DriveAction action() {
        final IBot bot = commander.getBot();
        final ArenaMap arenaMap = ArenaMap.getInstance();
        final Wall nearestWall = arenaMap.getNearestWall(bot);
        final double degreeToWall = nearestWall.degreeTo(bot);

        double turnDegree;
        double speed = Constants.MAX_SPEED;
        final double wallEscapeAngleOffset = 30;
        if (degreeToWall == 0) {
            turnDegree = 90 + wallEscapeAngleOffset;
        } else {
            final Direction direction = (degreeToWall < 0) ? Direction.LEFT : Direction.RIGHT;
            if (bot.getSpeed() < 0) {
                speed = -speed;
                turnDegree = bot.normalizeRelativeAngle((90 + Math.abs(degreeToWall) + wallEscapeAngleOffset) * direction.value);
            } else {
                turnDegree = bot.normalizeRelativeAngle((90 - Math.abs(degreeToWall) + wallEscapeAngleOffset) * direction.value);
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
                    // 進行方向の近くに敵がいたら進行方向を変える
                    final double degreeToEnemy = bot.bearingTo(predictedEnemy.x, predictedEnemy.y);
                    if (speed > 0 && Math.abs(degreeToEnemy) < 45) {
                        turnDegree = bot.normalizeRelativeAngle(degreeToWall + 180);
                    } else if (speed < 0 && Math.abs(degreeToEnemy) > 135) {
                        turnDegree = degreeToWall;
                    }
                }
            }
        }

        if ((speed > 0 && nearestWall.isFacing(bot.getDirection())) ||
                (speed < 0 && nearestWall.getOppositeWall().isFacing(bot.getDirection()))) {
            final double distanceToWall = nearestWall.distanceTo(bot) - (Commander.BODY_SIZE / 2);
            final double flg = (speed >= 0) ? 1 : -1;
            if (distanceToWall - 5 < (Constants.MAX_SPEED + (Constants.DECELERATION * 3))) {
                speed = 0;
            } else if (distanceToWall - 5 < (Constants.MAX_SPEED + (Constants.DECELERATION * 2))) {
                speed = Math.min(Math.abs(Constants.DECELERATION), speed) * flg;
            } else if (distanceToWall - 5 < (Constants.MAX_SPEED + Constants.DECELERATION)) {
                speed = Math.min(Math.abs(Constants.DECELERATION * 2), speed) * flg;
            } else if (distanceToWall - 5 < Constants.MAX_SPEED) {
                speed = Math.min(Math.abs(Constants.DECELERATION * 3), speed) * flg;
            }
        }

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