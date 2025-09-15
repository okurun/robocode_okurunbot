package okurun.predictor.model;

import dev.robocode.tankroyale.botapi.graphics.Color;
import okurun.Util;
import okurun.arenamap.ArenaMap;
import okurun.predictor.PredictData;
import okurun.radaroperator.EnemyState;

public class SimplePredictModel extends PredictModel {
    public PredictData predict(EnemyState enemyState, int predictTurnNum) {
        final var arenaMap = ArenaMap.getInstance();
        final double turnDegree = enemyState.getTurnDegree();
        double[] newPos = enemyState.getPosition();
        double newHeading = enemyState.heading;
        double[] prevPos;
        if (enemyState.previousState == null) {
            prevPos = enemyState.getPosition();
        } else {
            prevPos = enemyState.previousState.getPosition();
        }
        for (int i = enemyState.scandTurnNum + 1; i <= predictTurnNum; i++) {
            final double[] tempPos = newPos;
            newPos = Util.calcPosition(
                newPos,
                newHeading, enemyState.velocity,
                turnDegree,
                1
            );
            newPos = arenaMap.keepPositionInArena(newPos, prevPos);
            newHeading += turnDegree;
            prevPos = tempPos;
        }
        return new PredictData(
            newPos[0], newPos[1],
            enemyState.velocity, newHeading, turnDegree,
            predictTurnNum, this
        );
    }

    @Override
    public Color getBulletColor() {
        return Util.RED_COLOR;
    }
}
