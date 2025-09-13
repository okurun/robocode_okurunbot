package okurun.predictor.model;

import okurun.arenamap.ArenaMap;
import okurun.predictor.PredictData;
import okurun.predictor.Util;
import okurun.radaroperator.EnemyState;

public class SimplePredictModel extends PredictModel {
    public PredictData predict(EnemyState enemyState, int predictTurnNum) {
        final var arenaMap = ArenaMap.getInstance();
        final double turnDegree = enemyState.getTurnDegree();
        double[] newPos = new double[] {enemyState.x, enemyState.y};
        double newHeading = enemyState.heading;
        double[] prevPos;
        if (enemyState.previousState == null) {
            prevPos = new double[] {enemyState.x, enemyState.y};
        } else {
            prevPos = new double[] {enemyState.previousState.x, enemyState.previousState.y};
        }
        for (int i = enemyState.scandTurnNum + 1; i <= predictTurnNum; i++) {
            final double[] tempPos = newPos;
            newPos = Util.calcPosition(
                newPos[0], newPos[1],
                newHeading, enemyState.velocity,
                turnDegree,
                1
            );
            newPos = arenaMap.keepPositionInArena(newPos[0], newPos[1], prevPos[0], prevPos[1]);
            newHeading += turnDegree;
            prevPos = tempPos;
        }
        return new PredictData(
            newPos[0], newPos[1],
            enemyState.velocity, newHeading, turnDegree,
            predictTurnNum, this
        );
    }
}
