package okurun.driver.action;

import dev.robocode.tankroyale.botapi.Constants;
import dev.robocode.tankroyale.botapi.IBot;
import dev.robocode.tankroyale.botapi.graphics.Color;
import okurun.Commander;
import okurun.Util;
import okurun.Util.Direction;
import okurun.predictor.PredictData;
import okurun.predictor.Predictor;
import okurun.radaroperator.EnemyState;

public class SideMoveDriveAction extends AbstractDriveAction {
    private final EnemyState enemy;
    private final double distance;

    public SideMoveDriveAction(Commander commander, EnemyState enemy, double distance) {
        super(commander);
        this.enemy = enemy;
        this.distance = distance;
    }

    @Override
    public DriveAction action() {
        final IBot bot = commander.getBot();
        double turnDegree = 0;
        double speed = Constants.MAX_SPEED;
        if (enemy != null) {
            final Predictor predictor = Predictor.getInstance();
            final PredictData predictData = predictor.predict(enemy, bot.getTurnNumber());
            final double degreeToEnemy;
            final double distanceToEnemy;
            if (predictData != null) {
                degreeToEnemy = bot.bearingTo(predictData.x, predictData.y);
                distanceToEnemy = bot.distanceTo(predictData.x, predictData.y);
            } else {
                degreeToEnemy = bot.bearingTo(enemy.x, enemy.y);
                distanceToEnemy = bot.distanceTo(enemy.x, enemy.y);
            }
            final Direction direction = (degreeToEnemy < 0) ? Direction.LEFT : Direction.RIGHT;
            double baseDegree = 90;
            if (distanceToEnemy < this.distance) {
                baseDegree += 10;
            } else if (distanceToEnemy > this.distance) {
                baseDegree -= 10;
            }
            turnDegree = (baseDegree - Math.abs(degreeToEnemy)) * direction.value;
        }
        this.turnDegree = bot.normalizeRelativeAngle(turnDegree);
        this.targetSpeed = speed;
        return null;
    }

    @Override
    public Color getTracksColor() {
        return Util.GRAY_COLOR;
    }

}
