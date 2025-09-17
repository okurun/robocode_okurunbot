package okurun.driver.action;

import dev.robocode.tankroyale.botapi.Constants;
import dev.robocode.tankroyale.botapi.IBot;
import okurun.Commander;
import okurun.Util;
import okurun.Util.Direction;
import okurun.predictor.PredictData;
import okurun.predictor.Predictor;
import okurun.radaroperator.EnemyState;

public class ApproachEnemyDriveAction extends AbstractDriveAction {
    private final EnemyState enemy;
    private final double distance;

    public ApproachEnemyDriveAction(Commander commander, EnemyState enemy, double distance) {
        super(commander);
        this.enemy = enemy;
        this.distance = distance;
    }

    @Override
    public DriveAction action() {
        if (enemy == null) {
            this.turnDegree = 0;
            this.forwardDistance = 0;
            this.maxSpeed = 0;
            return null;
        }

        this.turnDegree = calcTurnDegree();
        this.forwardDistance = 0;
        this.maxSpeed = Constants.MAX_SPEED;

        return null;
    }

    private double calcTurnDegree() {
        final IBot bot = commander.getBot();
        final Predictor predictor = Predictor.getInstance();
        final PredictData predictData = predictor.predict(enemy, bot.getTurnNumber());
        final double[] pos;
        if (predictData == null) {
            pos = enemy.getPosition();
        } else {
            pos = predictData.getPosition();
        }
        final double angleToEnemy = bot.bearingTo(pos[0], pos[1]);
        final Direction direction = (angleToEnemy < 0) ? Direction.LEFT : Direction.RIGHT;
        final double[] targetPos = Util.calcPosition(
            pos,
            bot.normalizeRelativeAngle(angleToEnemy + (90 * direction.value)),
            this.distance,
            1
        );
        return bot.bearingTo(targetPos[0], targetPos[1]);
    }
}