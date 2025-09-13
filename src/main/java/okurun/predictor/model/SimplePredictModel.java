package okurun.predictor.model;

import okurun.arenamap.ArenaMap;
import okurun.predictor.PredictData;
import okurun.predictor.Util;
import okurun.radaroperator.EnemyState;

public class SimplePredictModel extends PredictModel {
    public PredictData predict(EnemyState enemyState, int predictTurnNum) {
        if (predictTurnNum <= 0) {
            return new PredictData(
                enemyState.x, enemyState.y,
                enemyState.velocity, enemyState.heading, enemyState.getTurnDegree(),
                predictTurnNum, this
            );
        }
        final var arenaMap = ArenaMap.getInstance();
        final double turnDegree = enemyState.getTurnDegree();
        double[] prevPos = new double[] {enemyState.x, enemyState.y};
        double[] newPos = Util.calcPosition(
                prevPos[0], prevPos[1],
                enemyState.heading, enemyState.velocity,
                turnDegree,
                1
            );
        newPos = arenaMap.keepPositionInArena(newPos[0], newPos[1], enemyState.x, enemyState.y);
        double newHeading = enemyState.heading + turnDegree;
        prevPos = newPos;
        if (predictTurnNum > 1) {
            for (int i = enemyState.scandTurnNum + 2; i <= predictTurnNum; i++) {
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
        }
        return new PredictData(
            newPos[0], newPos[1],
            enemyState.velocity, enemyState.heading, turnDegree,
            predictTurnNum, this
        );
    }
}
