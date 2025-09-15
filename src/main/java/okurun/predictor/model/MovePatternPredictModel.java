package okurun.predictor.model;

import java.util.ArrayList;
import java.util.List;

import okurun.arenamap.ArenaMap;
import okurun.predictor.PredictData;
import okurun.predictor.Util;
import okurun.radaroperator.EnemyState;

public class MovePatternPredictModel extends PredictModel {
    @Override
    public PredictData predict(EnemyState enemyState, int predictTurnNum) {
        final var arenaMap = ArenaMap.getInstance();
        List<MovePattern> movePatterns = extractMovePatterns(enemyState);
        if (movePatterns.isEmpty()) {
            return null; // Not enough data to predict
        }
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
            final MovePattern pattern = movePatterns.get(i % movePatterns.size());
            newPos = Util.calcPosition(newPos[0], newPos[1], newHeading, pattern.velocity, pattern.turnDegree, 1);
            newPos = arenaMap.keepPositionInArena(newPos[0], newPos[1], prevPos[0], prevPos[1]);
            newHeading += pattern.turnDegree;
            prevPos = tempPos;
        }
        return new PredictData(
            newPos[0], newPos[1],
            enemyState.velocity, newHeading, 0,
            predictTurnNum, this
        );
    }

    private List<MovePattern> extractMovePatterns(EnemyState enemyState) {
        List<MovePattern> patterns = new ArrayList<>();
        EnemyState current = enemyState;
        while (current.previousState != null && patterns.size() < 10) {
            if (current.previousState.scandTurnNum == 0) {
                break;
            }
            double velocity = current.velocity;
            double turnDegree = current.getTurnDegree();
            patterns.add(new MovePattern(velocity, turnDegree));
            current = current.previousState;
        }
        return patterns.reversed();
    }

    private static class MovePattern {
        public final double velocity;
        public final double turnDegree;

        public MovePattern(double velocity, double turnDegree) {
            this.velocity = velocity;
            this.turnDegree = turnDegree;
        }
    }
}
