package okurun.radaroperator.action;

import dev.robocode.tankroyale.botapi.IBot;
import okurun.Commander;
import okurun.radaroperator.EnemyState;

public class ScanTargetRadarAction extends AbstractRadarAction {
    private final int enemyId;

    public ScanTargetRadarAction(Commander commander, int enemyId) {
        super(commander);
        this.enemyId = enemyId;
    }

    @Override
    public RadarAction action() {
        final EnemyState enemy = commander.getRadarOperator().getEnemyState(enemyId);
        if (enemy == null) {
            this.turnRadarDegree = 360;
            return null;
        }
        final IBot bot = commander.getBot();
        double turnDegree = bot.radarBearingTo(enemy.x, enemy.y);
        if (turnDegree < 0) {
            turnDegree -= 20; // 少しオーバーランさせる
        } else {
            turnDegree += 20; // 少しオーバーランさせる
        }
        this.turnRadarDegree = turnDegree;
        return null;
    }
}
