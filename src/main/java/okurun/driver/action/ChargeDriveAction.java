package okurun.driver.action;

import dev.robocode.tankroyale.botapi.Constants;
import dev.robocode.tankroyale.botapi.IBot;
import dev.robocode.tankroyale.botapi.graphics.Color;
import okurun.Commander;
import okurun.Util;
import okurun.predictor.PredictData;
import okurun.predictor.Predictor;
import okurun.radaroperator.EnemyState;

/**
 * 敵の向かって突撃するDriveAction
 */
public class ChargeDriveAction extends AbstractDriveAction {
    private final EnemyState enemy;

    public ChargeDriveAction(Commander commander, EnemyState enemy) {
        super(commander);
        this.enemy = enemy;
    }

    @Override
    public DriveAction action() {
        final IBot bot = commander.getBot();
        final Predictor predictor = Predictor.getInstance();
        final PredictData predictData = predictor.predict(enemy, bot.getTurnNumber());
        final double degreeToEnemy;
        if (predictData != null) {
            degreeToEnemy = bot.bearingTo(predictData.x, predictData.y);
        } else {
            degreeToEnemy = bot.bearingTo(enemy.x, enemy.y);
        }
        this.turnDegree = degreeToEnemy;
        this.targetSpeed = Constants.MAX_SPEED;
        return null;
    }

    @Override
    public Color getTracksColor() {
        return Util.RED_COLOR;
    }
}
