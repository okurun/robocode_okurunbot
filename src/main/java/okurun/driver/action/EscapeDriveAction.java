package okurun.driver.action;

import dev.robocode.tankroyale.botapi.Constants;
import dev.robocode.tankroyale.botapi.IBot;
import okurun.Commander;
import okurun.predictor.PredictData;
import okurun.predictor.Predictor;
import okurun.radaroperator.EnemyState;

public class EscapeDriveAction extends AbstractDriveAction {
    private final EnemyState enemy;

    public EscapeDriveAction(Commander commander, EnemyState enemy) {
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
        this.turnDegree = bot.normalizeRelativeAngle(degreeToEnemy + 180);
        this.forwardDistance = 30;
        this.targetSpeed = Constants.MAX_SPEED;
        return null;
    }
    
}
