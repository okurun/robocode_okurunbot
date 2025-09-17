package okurun.predictor.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.robocode.tankroyale.botapi.graphics.Color;
import okurun.Util;
import okurun.arenamap.ArenaMap;
import okurun.predictor.PredictData;
import okurun.radaroperator.EnemyState;

public class MovePatternPredictModel extends PredictModel {
    private final Map<Integer, PredictData> cache = new HashMap<>();

    @Override
    public PredictData predict(EnemyState enemyState, int predictTurnNum) {
        final var arenaMap = ArenaMap.getInstance();
        List<MovePattern> movePatterns = extractMovePatterns(enemyState);
        if (movePatterns.size() < 10) {
            return null;
        }
        PredictData newData = new PredictData(
            enemyState.getPosition(),
            enemyState.heading, enemyState.velocity, enemyState.getTurnDegree(),
            enemyState.scandTurnNum, this
        );
        PredictData prevData;
        if (enemyState.previousState == null) {
            prevData = newData;
        } else {
            prevData = new PredictData(
                enemyState.previousState.getPosition(),
                enemyState.previousState.heading, enemyState.previousState.velocity, enemyState.previousState.getTurnDegree(),
                enemyState.previousState.scandTurnNum, this
            );
        }
        for (int i = enemyState.scandTurnNum + 1; i <= predictTurnNum; i++) {
            if (cache.containsKey(i)) {
                prevData = newData;
                newData = cache.get(i);
                continue;
            }
            final PredictData tempData = newData;
            final MovePattern pattern = movePatterns.get(i % movePatterns.size());
            double[] newPos = Util.calcPosition(
                newData.getPosition(),
                newData.heading,
                pattern.velocity,
                pattern.turnDegree,
                1
            );
            newPos = arenaMap.keepPositionInArena(newPos, prevData.getPosition());
            newData = new PredictData(
                newPos,
                newData.heading + pattern.turnDegree,
                newData.velocity, pattern.turnDegree,
                i, this
            );
            prevData = tempData;
            cache.put(i, newData);
        }
        return newData;
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

    @Override
    public Color getBulletColor() {
        return Util.YELLOW_COLOR;
    }

    @Override
    public void clearCache() {
        cache.clear();
    }
}
