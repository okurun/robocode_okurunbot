package okurun.radaroperator.action;

import dev.robocode.tankroyale.botapi.IBot;
import okurun.Commander;
import okurun.predictor.PredictData;
import okurun.predictor.Predictor;
import okurun.radaroperator.EnemyState;

public class ScanTargetRadarAction extends AbstractRadarAction {
    private final int enemyId;

    public ScanTargetRadarAction(Commander commander, int enemyId) {
        super(commander);
        this.enemyId = enemyId;
    }

    @Override
    public RadarAction action() {
        final IBot bot = commander.getBot();
        final EnemyState enemy = commander.getRadarOperator().getEnemyState(enemyId);
        if (enemy == null || bot.getTurnNumber() - enemy.scandTurnNum > 10) {
            this.turnRadarDegree = 360;
            return null;
        }
        final Predictor predictor = Predictor.getInstance();
        final PredictData predictData = predictor.predict(enemy, bot.getTurnNumber());
        double turnDegree = bot.radarBearingTo(predictData.x, predictData.y);
        if (turnDegree < 0) {
            turnDegree -= 20; // 少しオーバーランさせる
        } else {
            turnDegree += 20; // 少しオーバーランさせる
        }
        this.turnRadarDegree = turnDegree;
        return null;
    }
}
